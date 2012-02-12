/*
 * Copyright (c) 2011-2012 Wave2 Limited. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of Wave2 Limited nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.wave2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 *
 * @author Alan Snelson
 */
public class XMPPWrite {

    @Option(name = "--help")
    private boolean help;
    @Option(name = "-s", usage = "XMPP Server")
    private String servername;
    @Option(name = "-u", usage = "Username")
    private String username;
    @Option(name = "-p", usage = "Password")
    private String password;
    @Option(name = "-v")
    public static boolean verbose;
    @Argument
    private List<String> arguments = new ArrayList<>();
    //Application properties
    private final Properties properties = new Properties();

    /**
     * Load application properties
     */
    private void loadProperties() {
        try {
            properties.load(getClass().getResourceAsStream("/application.properties"));
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * Default constructor for XMPPWrite.
     */
    public XMPPWrite() {
        loadProperties();
    }

    /**
     * Get XMPPWrite version
     *
     * @return XMPPWrite version
     */
    public String getVersion() {
        return properties.getProperty("application.version");
    }

    /**
     * Main entry point for XMPPWrite when run from command line
     *
     * @param  args  Command line arguments
     */
    public static void main(String[] args) throws IOException {
        new XMPPWrite().doMain(args);
    }

    /**
     * @param args the command line arguments
     */
    public void doMain(String[] args) throws IOException {

        String usage = "Usage: java -jar XMPPWrite.jar [-s xmpp server] [-u username] [-p password] [ message ... ]\nOptions:\n    -s  XMPP Server\n    -u  Username\n    -p  Password\n";
        CmdLineParser parser = new CmdLineParser(this);

        // if you have a wider console, you could increase the value;
        // here 80 is also the default
        parser.setUsageWidth(80);

        try {
            // parse the arguments.
            parser.parseArgument(args);

            // after parsing arguments, you should check
            // if enough arguments are given.
            if (arguments.isEmpty()) {
                throw new CmdLineException(parser, "Print Help");
            }

        }
        catch (CmdLineException e) {
            if (e.getMessage().equalsIgnoreCase("Print Help")) {
                System.err.println("XMPPWrite.java " + getVersion() + "\nThis software comes with ABSOLUTELY NO WARRANTY. This is free software,\nand you are welcome to modify and redistribute it under the BSD license" + "\n\n" + usage);
                return;
            }
            // if there's a problem in the command line,
            // you'll get this exception. this will report
            // an error message.
            System.err.println(e.getMessage());
            // print usage.
            System.err.println(usage);
            return;
        }

        XMPPConnection connection = new XMPPConnection(servername);
        try {
            connection.connect();
            connection.login(username, password);
            ChatManager chatmanager = connection.getChatManager();

            Message message;

            Chat chat = chatmanager.createChat(args[0], new MessageListener() {

                public void processMessage(Chat chat, Message message) {
                    //System.out.println("Received message: " + message);
                }
                
            });
            chat.sendMessage(args[1]);
        }
        catch (XMPPException e) {
            System.out.println(e.getMessage());
        }

    }
}