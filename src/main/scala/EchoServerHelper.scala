import java.io.{BufferedReader, BufferedWriter, InputStreamReader, OutputStreamWriter}
import java.net.Socket

import scala.io.Source

class EchoServerHelper {

  val error_response: String = "HTTP/1.1 404 Not Found\r\n\r\n"
  val ok_response: String = "HTTP/1.1 200 OK\r\n\r\n"
  val invalid_response: String = "HTTP/1.1 501 Not Implemented\r\n\r\n"

  def serve(s: Socket) = {
    System.out.print("recevied..")
    val in = new BufferedReader(new InputStreamReader(s.getInputStream()))
    val out = new BufferedWriter((new OutputStreamWriter(s.getOutputStream())))
    readAndWrite(in,out)
    s.close()
  }

  def readAndWrite(in: BufferedReader, out: BufferedWriter): Unit = {
    val url = parseURL(in.readLine()) //read response
    if(url(0) == "GET") {
      processGet(url(1),out)
    } else {
      //System.out.println("non get request provided") //debug code
      out.write(invalid_response)
    }
    //System.out.println("one iteration!!") //debug code
    out.flush()
    in.close()
    out.close()
  }

  def parseURL(url:String): Array[String] = {
    //System.out.println("response: " + url)
    val parameters = url.split(" ")
    if(parameters(1)=="/") {
      parameters(1) = "index.html"
    } else {
      parameters(1) = parameters(1).substring(1, parameters(1).length())
    }
    //System.out.println("in parseURL")
    parameters
  }

  def processGet(filename:String, out:BufferedWriter) {
    if(isValidUrl(filename)) { //if filename exists
      //System.out.println("responding back with valid file") //debug code
      out.write(ok_response)
      for (line <- Source.fromFile(filename).getLines) {
        //System.out.println(line)
        out.write(line)
        out.newLine()
      }
    } else { //if not found return 404 error
      //System.out.println("responding back with 404") //debug code
      out.write(error_response)
    }
  }

  def isValidUrl(filename:String) : Boolean = {
    //System.out.println("in isValid") //debug code
    //System.out.println("filename: " + filename)
    new java.io.File(filename).exists
  }
}
