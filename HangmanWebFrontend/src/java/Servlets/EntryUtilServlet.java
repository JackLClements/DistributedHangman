/*
 */
package Servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import network.*;

/**
 *
 * @author Jack L. Clements
 */
@WebServlet(urlPatterns = {"/someservlet/*"}, asyncSupported = true)
public class EntryUtilServlet extends HttpServlet {

    private static final Queue<AsyncContext> QUEUE = new ConcurrentLinkedQueue();
    private Thread connectionThread;
    private static volatile Connection con1;
    private static final ArrayList<Message> MSG_QUEUE = new ArrayList<>();

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods. 
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Deprecated 
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet NewServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet NewServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        System.out.println(action);
        if (action.equals("choice")) {
            connect();
            AsyncContext asyncContext = request.startAsync(request, response);
            asyncContext.setTimeout(1000); //only updates on timeout
            asyncContext.addListener(new AsyncListener() {
                @Override
                public void onComplete(AsyncEvent event) throws IOException {
                    QUEUE.remove(asyncContext);
                }

                @Override
                public void onTimeout(AsyncEvent event) throws IOException {
                    QUEUE.remove(asyncContext);
                }

                @Override
                public void onError(AsyncEvent event) throws IOException {
                    QUEUE.remove(asyncContext);
                }

                @Override
                public void onStartAsync(AsyncEvent event) throws IOException {
                    //no need for any of this
                }
            });
            QUEUE.add(asyncContext);

            PrintWriter out = response.getWriter();
            for (Message msg : MSG_QUEUE) {
                System.out.println("MESSAGE IN QUEUE AT CONNECTION TIME - " + msg.getHeader());
                synchronized (MSG_QUEUE) {
                    if (msg instanceof CurrentGamesMessage) {
                        out.println((((CurrentGamesMessage) msg).getGames()));
                    }
                }
            }
            out.flush();
        }
        if (action.equals("getUpdates")) {
            PrintWriter out = response.getWriter();
            if (con1.isActive()) {
                for (Message msg : MSG_QUEUE) {
                    synchronized (MSG_QUEUE) {
                        System.out.println("Queued Message - " + msg.getHeader());
                        out.println(msg.getHeader());
                        if (msg instanceof GameEndMessage) {
                            out.println("<li>Game Over</li>");
                            boolean winLose = ((GameEndMessage) msg).getWin();
                            if (winLose) {
                                out.println("<li>You win!</li>");
                            } else {
                                out.println("<li>You lose.</li>");
                            }
                            con1.send(new GameEndMessage("Disconnect", true)); //class doubles as a d/c

                        }
                        if (msg instanceof GameStateMessage) {
                            out.println("<li>Guesses - " + ((GameStateMessage) msg).getScore() + "</li>\n");
                            out.println("<li>Word so far - " + String.copyValueOf(((GameStateMessage) msg).getWord()) + "<li>\n");
                        }
                        //need to remove objs. from queue at some point
                    }
                }
            } else {
                out.println("<li>Error: Not connected to server. Please connect and try again. </li>");
            }
            out.flush();
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        //processRequest(request, response);
        if (action.equals("chooseRoom")) {
            String roomidStr = request.getParameter("roomid");
            int roomid = Integer.parseInt(roomidStr);
            if (roomid == 0) {
                con1.send(new ChooseGameMessage("Choice - ", true));
            } else {
                con1.send(new ChooseGameMessage("Choice", false, roomid));
            }
        }
        if (action.equals("guess")) {
            String character = request.getParameter("character");
            System.out.println("CHARACTER = " + character);
            char appendChar = character.toLowerCase().charAt(0);
            System.out.println("Guessing " + appendChar);
            con1.send(new GuessMessage("Guess - ", appendChar));
        }
    }

    @Override
    public void init() {
        try {
            /*
            InetAddress server = InetAddress.getByName("localhost");
            Socket connection = new Socket(server, 55552);
            System.out.println("Connected");
            con1 = new Connection(connection);
            con1.setServlet(this);
            connectionThread = new Thread(con1);
            connectionThread.start();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Connect to server
     */
    public void connect() {
        try {
            System.out.println("Closing connection");
            if(con1 != null){ //close existing connection
                con1.close();
                MSG_QUEUE.clear();
            }
            
            InetAddress server = InetAddress.getByName("localhost");
            Socket connection = new Socket(server, 55552);
            System.out.println("Connected");
            con1 = new Connection(connection);
            con1.setServlet(this);
            connectionThread = new Thread(con1);
            connectionThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void processMessage(Message msg) throws IOException {
        System.out.println(msg.getHeader());
        MSG_QUEUE.add(msg);
        System.out.println("Execution complete");
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet to process messages between client and server";
    }// </editor-fold>

}
