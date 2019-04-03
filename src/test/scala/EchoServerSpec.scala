import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.net.{ServerSocket, Socket}

import org.scalatest._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar

/* Steven Wancewicz
 */
class EchoServerSpec extends FlatSpec with Matchers with MockitoSugar {

  "parseURL" should "return a list with response type, filename, and http type" in {
    EchoServer.parseURL("GET /test.html HTTP/1.1")(0) should be ("GET")
    EchoServer.parseURL("GET /test.html HTTP/1.1")(1) should be ("test.html")
    EchoServer.parseURL("GET /test.html HTTP/1.1")(2) should be ("HTTP/1.1")
  }

  it should "return index.html with an '/' filename" in {
    EchoServer.parseURL("GET / HTTP/1.1")(1) should be ("index.html")
  }

  it should "return new/index.html with a longer path filename" in {
    EchoServer.parseURL("GET /new/index.html HTTP/1.1")(1) should be ("new/index.html")
  }

  "isValidURL" should "return true if a valid file is requested" in {
    EchoServer.isValidUrl("test.html") should be (true)
  }

  it should "return false if a non existant file is request" in {
    EchoServer.isValidUrl("invalid.help") should be (false)
  }

  "Serve" should "return a 404 error for an invalid GET request" in {

    //val serverSocket = mock[ServerSocket]
    val socket = mock[Socket]
    //when(serverSocket.accept()).thenReturn(socket)
    val byteArrayOutputStream = new ByteArrayOutputStream()
    val byteArrayInputStream = new ByteArrayInputStream("GET /fake.html HTTP/1.1".getBytes())

    when(socket.getOutputStream).thenReturn(byteArrayOutputStream)
    when(socket.getInputStream).thenReturn(byteArrayInputStream)

    EchoServer.serve(socket)

    byteArrayOutputStream.toString() should be(EchoServer.error_response)
    verify(socket).close()
  }

  it should "return a 501 for an invalid request" in {
    val socket = mock[Socket]
    val byteArrayOutputStream = new ByteArrayOutputStream()
    val byteArrayInputStream = new ByteArrayInputStream("Invalid request".getBytes())

    when(socket.getOutputStream).thenReturn(byteArrayOutputStream)
    when(socket.getInputStream).thenReturn(byteArrayInputStream)

    EchoServer.serve(socket)

    byteArrayOutputStream.toString() should be(EchoServer.invalid_response)
    //verify(socket).close()
  }

  /*
  def main(arg: Array[String) {
    val server = new ServerSocket(9999)
    while(true) {
      val s = server.accept()
      my_server ! MySocket(s)
    }
  }
   */

}
