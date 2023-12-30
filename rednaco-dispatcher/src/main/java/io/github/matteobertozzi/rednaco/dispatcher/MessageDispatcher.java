/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.matteobertozzi.rednaco.dispatcher;

import java.util.regex.Matcher;

import io.github.matteobertozzi.easerinsights.logging.Logger;
import io.github.matteobertozzi.rednaco.data.JsonFormat;
import io.github.matteobertozzi.rednaco.dispatcher.message.Message;
import io.github.matteobertozzi.rednaco.dispatcher.message.MessageError;
import io.github.matteobertozzi.rednaco.dispatcher.message.MessageException;
import io.github.matteobertozzi.rednaco.dispatcher.message.MessageUtil;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutePathUtil;
import io.github.matteobertozzi.rednaco.dispatcher.routing.Router;
import io.github.matteobertozzi.rednaco.dispatcher.routing.RoutesMapping.RouteMatcher;
import io.github.matteobertozzi.rednaco.dispatcher.routing.UriMessage;
import io.github.matteobertozzi.rednaco.dispatcher.session.AuthSession;
import io.github.matteobertozzi.rednaco.dispatcher.session.AuthSessionProvider;

public class MessageDispatcher {
  private final DispatcherProviders providers = new DispatcherProviders();
  private Router router;

  public Message execute(final DispatcherContext ctx, final UriMessage message) {
    final RouteMatcher mapping = router.get(message.method(), RoutePathUtil.cleanPath(message.path()));
    if (mapping == null) {
      return MessageUtil.newErrorMessage(MessageError.notFound());
    }

    try {
      ctx.setMatcher(mapping.matcher());
      return mapping.executor().execute(ctx, message);
    } catch (final Throwable e) {
      Logger.error(e, "execution failed {} {}", message.method(), message.path());
      return MessageUtil.newErrorMessage(MessageError.internalServerError());
    }
  }

  public DispatcherProviders providers() {
    return providers;
  }

  public void setRouter(final Router router) {
    this.router = router;
  }

  public void setAuthSessionProvider(final AuthSessionProvider provider) {
    providers.sessionProvider = provider;
  }

  public static abstract class DispatcherContext implements MessageContext {
    private Matcher matcher = null;

    private void setMatcher(final Matcher matcher) {
      this.matcher = matcher;
    }

    public boolean hasPathVariables() {
      return matcher != null;
    }

    public String pathVariable(final String name) { return matcher.group(name); }
    public <T> T pathVariable(final String name, final Class<T> classOfT) {
      return JsonFormat.INSTANCE.fromString(matcher.group(name), classOfT);
    }

    public String pathPatternVariable(final int index) { return matcher.group(index); }
    public <T> T pathPatternVariable(final int index, final Class<T> classOfT) {
      return JsonFormat.INSTANCE.fromString(matcher.group(index), classOfT);
    }
  }

  public static final class DispatcherProviders implements AuthSessionProvider {
    private AuthSessionProvider sessionProvider;

    @Override
    public <T extends AuthSession> T verifySession(final Message message, final Class<T> classOfT) throws MessageException{
      return sessionProvider.verifySession(message, classOfT);
    }

    @Override
    public void requirePermissions(final AuthSession session, final String module, final String[] actions) throws MessageException {
      sessionProvider.requirePermissions(session, module, actions);
    }

    @Override
    public void requireOneOfPermission(final AuthSession session, final String module, final String[] actions) throws MessageException{
      sessionProvider.requireOneOfPermission(session, module, actions);
    }
  }
}
