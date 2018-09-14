/*
 * sbt
 * Copyright 2011 - 2018, Lightbend, Inc.
 * Copyright 2008 - 2010, Mark Harrah
 * Licensed under Apache License 2.0 (see LICENSE)
 */

package sbt

// Todo share this this io.syntax
private[sbt] trait IOSyntax0 extends IOSyntax1 {
  implicit def alternative[A, B](f: A => Option[B]): Alternative[A, B] =
    g => a => f(a) orElse g(a)
}
private[sbt] trait Alternative[A, B] {
  def |(g: A => Option[B]): A => Option[B]
}

private[sbt] trait IOSyntax1 {
  implicit def singleFileFinder(file: File): sbt.io.PathFinder = sbt.io.PathFinder(file)
}
