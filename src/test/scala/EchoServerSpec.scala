import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.net.{Socket}

import org.scalatest._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar


/* Steven Wancewicz
 */
class EchoServerSpec extends FlatSpec with Matchers with MockitoSugar {

  "parseURL" should "return a list with response type, filename, and http type" in {
    val echo = new EchoServerHelper();
    echo.parseURL("GET /test.html HTTP/1.1")(0) should be ("GET")
    echo.parseURL("GET /test.html HTTP/1.1")(1) should be ("test.html")
    echo.parseURL("GET /test.html HTTP/1.1")(2) should be ("HTTP/1.1")
  }

  it should "return index.html with an '/' filename" in {
    val echo = new EchoServerHelper();
    echo.parseURL("GET / HTTP/1.1")(1) should be ("index.html")
  }

  it should "return new/index.html with a longer path filename" in {
    val echo = new EchoServerHelper();
    echo.parseURL("GET /new/index.html HTTP/1.1")(1) should be ("new/index.html")
  }

  "isValidURL" should "return true if a valid file is requested" in {
    val echo = new EchoServerHelper();
    echo.isValidUrl("test.html") should be (true)
  }

  it should "return false if a non existant file is request" in {
    val echo = new EchoServerHelper();
    echo.isValidUrl("invalid.help") should be (false)
  }

  "Serve" should "return a 404 error for an invalid GET request" in {

    //val serverSocket = mock[ServerSocket]
    val socket = mock[Socket]
    //when(serverSocket.accept()).thenReturn(socket)
    val byteArrayOutputStream = new ByteArrayOutputStream()
    val byteArrayInputStream = new ByteArrayInputStream("GET /fake.html HTTP/1.1".getBytes())
    val echo = new EchoServerHelper();

    when(socket.getOutputStream).thenReturn(byteArrayOutputStream)
    when(socket.getInputStream).thenReturn(byteArrayInputStream)

    echo.serve(socket)

    byteArrayOutputStream.toString() should be(echo.error_response)
    verify(socket).close()
  }

  it should "return a 501 for an invalid request" in {
    val socket = mock[Socket]
    val byteArrayOutputStream = new ByteArrayOutputStream()
    val byteArrayInputStream = new ByteArrayInputStream("Invalid request".getBytes())
    val echo = new EchoServerHelper();

    when(socket.getOutputStream).thenReturn(byteArrayOutputStream)
    when(socket.getInputStream).thenReturn(byteArrayInputStream)

    echo.serve(socket)

    byteArrayOutputStream.toString() should be(echo.invalid_response)
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
