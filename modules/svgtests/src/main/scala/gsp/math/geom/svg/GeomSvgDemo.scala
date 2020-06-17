// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gsp.math.geom.svg

import org.scalajs.dom
import org.scalajs.dom.ext._
import scala.scalajs.js.annotation._
import gsp.math.geom.jts.interpreter._
import gsp.math.geom.svg._
import gsp.math.geom.svg.implicits._
import gsp.math.geom.ShapeExpression
import gsp.math.geom.syntax.all._
import gem.geom.GmosScienceAreaGeometry
import gem.geom.GmosOiwfsProbeArm
import gsp.math.geom.jts.JtsShape
import gsp.math.syntax.int._
import gsp.math.Angle
import gsp.math.Offset
import gpp.svgdotjs.svgdotjsSvgJs.mod.SVG_
import gpp.svgdotjs.svgdotjsSvgJs.mod.Svg
import gem.enum.GmosNorthFpu
import gem.enum.GmosSouthFpu
import gem.enum.PortDisposition
import cats.data.NonEmptyList

@JSExportTopLevel("SVGTest")
object GeomSvgDemo {

  val posAngle: Angle =
    145.deg

  val guideStarOffset: Offset =
    Offset(170543999.µas.p, -24177003.µas.q)

  val offsetPos: Offset =
    Offset(60.arcsec.p, 60.arcsec.q)

  val fpu: Option[Either[GmosNorthFpu, GmosSouthFpu]] =
    Some(Right(GmosSouthFpu.LongSlit_5_00))

  val port: PortDisposition =
    PortDisposition.Side

  // Shape to display
  val shapes: NonEmptyList[(String, ShapeExpression)] =
    NonEmptyList.of(
      ("probe", GmosOiwfsProbeArm.shapeAt(posAngle, guideStarOffset, offsetPos, fpu, port)),
      ("patrol-field", GmosOiwfsProbeArm.patrolFieldAt(posAngle, offsetPos, fpu, port)),
      ("science-area", GmosScienceAreaGeometry.shapeAt(posAngle, offsetPos, fpu))
    )

  // Scale
  val arcsecPerPixel: Double =
    1.0

  val gridSize: Angle =
    50.arcsec

  @JSExport
  def main(): Unit = {
    val container = Option(dom.document.getElementById("root")).getOrElse {
      val elem = dom.document.createElement("div")
      elem.id = "root"
      dom.document.body.appendChild(elem)
      elem
    }
    container.children.map(container.removeChild)

    val svg: Svg = SVG_()
    shapes
      .map(x => x.copy(_2 = x._2.eval))
      .map {
        case (id, jts: JtsShape) => (id, jts)
        case x                   => sys.error(s"Whoa unexpected shape type: $x")
      }
      .toSvg(svg)
    // val composite = shapes.map(_._2).fold(ShapeExpression.Empty)(_ ∪ _)
    // composite.eval match {
    //   case jts: JtsShape =>
    //     jts.toSvg(svg)
    //     assert(svg.children().length.toInt > 0)
    //   case x             => sys.error(s"Whoa unexpected shape type: $x")
    // }
    // shapes.foreach {
    //   case shape =>
    //     shape.eval match {
    //       case jts: JtsShape =>
    //         jts.toSvg(svg)
    //         assert(svg.children().length.toInt > 0)
    //       case x             => sys.error(s"Whoa unexpected shape type: $x")
    //     }
    // }
    // svg.width(720)
    // svg.height(720)
    svg.size(720, 720)
    container.appendChild(svg.node_Svg)
    ()
  }
}
