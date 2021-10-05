import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder

import java.io.{File, FileOutputStream, InputStream, OutputStream}
import scala.annotation.tailrec

object S3Download extends App {
  val s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build()
  val bucketName = "413742098112-static-website-bucket"
  val objectKey = "index.html"
  val s3Object = s3.getObject(bucketName, objectKey)
  val inputStream = s3Object.getObjectContent
  val outputStream = new FileOutputStream(new File(objectKey))
  val buffer: Array[Byte] = new Array[Byte](1024 * 10)

  val bytes = read(inputStream, outputStream, buffer, 0)
  println(s"Read $bytes from $bucketName:$objectKey")

  @tailrec
  def read(inputStream: InputStream, outputStream: OutputStream, buff: Array[Byte], acc: Long): Long = {
    val readBytes = inputStream.read(buff, 0, buff.length)
    if (readBytes > -1) {
      outputStream.write(buff, 0, readBytes)
      read(inputStream, outputStream, buff, acc + readBytes)
    } else {
      inputStream.close()
      outputStream.close()
      acc
    }
  }

}
