/* JUG Java Uuid Generator
 *
 * Copyright (c) 2002- Tatu Saloranta, tatu.saloranta@iki.fi
 *
 * Licensed under the License specified in the file LICENSE which is
 * included with the source code.
 * You may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fasterxml.uuid;

import org.slf4j.LoggerFactory;

/**
 * This is a holder for a {@link org.slf4j.Logger} instance so there's
 * no need to create a logger object per class. This also allows all
 * logging to be routed to a single logger name without having to create
 * a constant and use it in other classes.
 */
public class Logger
{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);

    public static org.slf4j.Logger getLogger() {
        return logger;
    }
}

