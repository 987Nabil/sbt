/*
 * sbt
 * Copyright 2011 - 2018, Lightbend, Inc.
 * Copyright 2008 - 2010, Mark Harrah
 * Licensed under Apache License 2.0 (see LICENSE)
 */

package sbt
package protocol

import java.io.File
import java.net.{ Socket, URI, InetAddress }
import sjsonnew.BasicJsonProtocol
import sjsonnew.support.scalajson.unsafe.{ Parser, Converter }
import sjsonnew.shaded.scalajson.ast.unsafe.JValue
import sbt.internal.protocol.{ PortFile, TokenFile }
import sbt.internal.protocol.codec.{ PortFileFormats, TokenFileFormats }
import sbt.internal.util.Util.isWindows
import org.scalasbt.ipcsocket._

object ClientSocket {
  private lazy val fileFormats = new BasicJsonProtocol with PortFileFormats with TokenFileFormats {}

  def socket(portfile: File): (Socket, Option[String]) = {
    import fileFormats._
    val json: JValue = Parser.parseFromFile(portfile).get
    val p = Converter.fromJson[PortFile](json).get
    val uri = new URI(p.uri)
    // println(uri)
    val token = p.tokenfilePath map { tp =>
      val tokeFile = new File(tp)
      val json: JValue = Parser.parseFromFile(tokeFile).get
      val t = Converter.fromJson[TokenFile](json).get
      t.token
    }
    val sk = uri.getScheme match {
      case "local" if isWindows =>
        new Win32NamedPipeSocket("""\\.\pipe\""" + uri.getSchemeSpecificPart)
      case "local" => new UnixDomainSocket(uri.getSchemeSpecificPart)
      case "tcp"   => new Socket(InetAddress.getByName(uri.getHost), uri.getPort)
      case _       => sys.error(s"Unsupported uri: $uri")
    }
    (sk, token)
  }
}
