package com.github.pshirshov.izumi.idealingua.translator.toscala.products

import com.github.pshirshov.izumi.idealingua.model.common.TypeName
import com.github.pshirshov.izumi.idealingua.translator.toscala.types.runtime.Import

import scala.meta.{Defn, Term}


final case class CogenProduct[T <: Defn](
                                          defn: T
                                          , xcompanion: Defn.Object
                                          , tools: Defn.Class
                                          , more: List[Defn] = List.empty
                                          , preamble: String = ""
                                        ) extends AccompaniedCogenProduct[T] {
  override def companion: Defn.Object = {
    import com.github.pshirshov.izumi.idealingua.translator.toscala.tools.ScalaMetaTools._
    val implicitClass = Seq(tools).filter(_.templ.stats.nonEmpty)
    xcompanion.appendDefinitions(implicitClass: _*)
  }
}

final case class CogenPair[T <: Defn](defn: T, companion: Defn.Object) {
  def render: List[Defn] = List(defn, companion)
}

final case class CogenServiceDefs(defs: Defn.Object, in: CogenPair[Defn.Trait], out: CogenPair[Defn.Trait]) {
  def render: Defn = {
    import com.github.pshirshov.izumi.idealingua.translator.toscala.tools.ScalaMetaTools._
    defs.prependDefnitions(in.render ++ out.render)
  }
}

final case class CogenServiceProduct(
                                      service: CogenPair[Defn.Trait]
                                      , client: CogenPair[Defn.Trait]
                                      , wrapped: CogenPair[Defn.Trait]
                                      , defs: CogenServiceDefs
                                      , imports: List[Import]
                                    ) extends RenderableCogenProduct {

  override def preamble: String =
    s"""${imports.map(_.render).mkString("\n")}
       |""".stripMargin

  def render: List[Defn] = {
    List(service, client, wrapped).flatMap(_.render) :+ defs.render
  }
}


object CogenProduct {
  type InterfaceProduct = CogenProduct[Defn.Trait]
  type CompositeProudct = CogenProduct[Defn.Class]
  type IdentifierProudct = CogenProduct[Defn.Class]

  final case class IfaceMirrorProduct(defn: Defn.Trait, more: List[Defn] = List.empty, preamble: String = "") extends UnaryCogenProduct[Defn.Trait]

  final case class EnumProduct(
                                defn: Defn.Trait
                                , companionBase: Defn.Object
                                , elements: List[(Term.Name, Defn)]
                                , more: List[Defn] = List.empty
                                , preamble: String = ""
                              ) extends AccompaniedCogenProduct[Defn.Trait] {
    override def companion: Defn.Object = {
      import com.github.pshirshov.izumi.idealingua.translator.toscala.tools.ScalaMetaTools._
      companionBase.appendDefinitions(elements.map(_._2))
    }
  }

  final case class AdtElementProduct[T <: Defn](
                                                 name: TypeName
                                                 , defn: T
                                                 , companion: Defn.Object
                                                 , converters: List[Defn.Def]
                                                 , more: List[Defn] = List.empty
                                                 , preamble: String = ""
                                               ) extends AccompaniedCogenProduct[T] {
    override def render: List[Defn] = List(defn) ++ more ++ converters ++ List(companion)
  }

  final case class AdtProduct(
                               defn: Defn.Trait
                               , companionBase: Defn.Object
                               , elements: List[AdtElementProduct[Defn.Class]]
                               , more: List[Defn] = List.empty
                               , preamble: String = ""
                             ) extends AccompaniedCogenProduct[Defn.Trait] {
    override def companion: Defn.Object = {
      import com.github.pshirshov.izumi.idealingua.translator.toscala.tools.ScalaMetaTools._
      companionBase.appendDefinitions(elements.flatMap(_.render))
    }
  }

}
