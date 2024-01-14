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

public class MessageStats {
  private long firstByteNs;   // First Byte Received
  private long headersNs;     // Headers Received
  private long fullMsgNs;     // Full Request Received
  private long queuePushNs;   // Request Added to the Queue
  private long paramParseNs;  // Param Parse Duration
  private long execStartNs;   // Execution Start
  private long execEndNs;     // Execution End
  private long lastByteWrNs;  // Last Byte Written

  public long firstByteNs() { return firstByteNs; }
  public long headersNs() { return headersNs; }
  public long fullMsgNs() { return fullMsgNs; }
  public long networkIoNs() { return fullMsgNs - firstByteNs; }
  public long queuePushNs() { return queuePushNs; }
  public long execStartNs() { return execStartNs; }
  public long queueTimeNs() { return execStartNs - queuePushNs; }
  public long paramParseNs() { return paramParseNs; }
  public long execEndNs() { return execEndNs; }
  public long execTimeNs() { return execEndNs - execStartNs; }
  public long lastByteWrNs() { return lastByteWrNs; }

  public void setFirstByteNs(final long firstByteNs) {
    this.firstByteNs = firstByteNs;
  }

  public void setHeadersNs(final long headersNs) {
    this.headersNs = headersNs;
  }

  public void setFullMsgNs(final long fullMsgNs) {
    this.fullMsgNs = fullMsgNs;
  }

  public void setQueuePushNs(final long queuePushNs) {
    this.queuePushNs = queuePushNs;
  }

  public void setParamParseNs(final long paramParseNs) {
    this.paramParseNs = paramParseNs;
  }

  public void execStartNs(final long execStartNs) {
    this.execStartNs = execStartNs;
  }

  public void setExecEndNs(final long execEndNs) {
    this.execEndNs = execEndNs;
  }

  public void setLastByteWrNs(final long lastByteWrNs) {
    this.lastByteWrNs = lastByteWrNs;
  }
}
