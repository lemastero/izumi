package com.github.pshirshov.izumi.idealingua.model.il.ast.typed

import com.github.pshirshov.izumi.idealingua.model.common

case class DomainId(pkg: common.Package, id: String) {
  override def toString: String = s"{${toPackage.mkString(".")}}"

  def toPackage: common.Package = pkg :+ id
}

object DomainId {
  final val Builtin = DomainId(Seq.empty, "/")
  final val Undefined = DomainId(Seq.empty, ".")
}
