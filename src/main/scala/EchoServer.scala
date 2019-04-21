import java.net._

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/*Steven Wancewicz
 */
object EchoServerActor {

  def main(args: Array[String]) {
    val server = new ServerSocket(9999)
    val system: ActorSystem = ActorSystem("echoServer")
    val socketActor: ActorRef = system.actorOf(EchoServerActor.props, "serverActor")

    while (true) {
      val s = server.accept()
      System.out.println("received..")
      socketActor ! s
    }
  }

  def props(): Props = Props(new EchoServerActor())
}
class EchoServerActor() extends Actor {

  def receive = {
    case s: Socket =>
      val echo = new EchoServerHelper()
      echo.serve(s)
    case _ =>
      System.out.println("something received...")
  }
}