// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gsp.math.geom.svg

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

final class GeomSvgSpec extends munit.FunSuite {

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
  val shapes: List[ShapeExpression] =
    List(
      GmosOiwfsProbeArm.shapeAt(posAngle, guideStarOffset, offsetPos, fpu, port),
      GmosOiwfsProbeArm.patrolFieldAt(posAngle, offsetPos, fpu, port),
      GmosScienceAreaGeometry.shapeAt(posAngle, offsetPos, fpu)
    )

  // Scale
  val arcsecPerPixel: Double =
    1.0

  val gridSize: Angle =
    50.arcsec

  test("basic") {
    val svg: Svg = SVG_()
    shapes.foreach {
      case shape =>
        shape.eval match {
          case jts: JtsShape =>
            jts.toSvg(svg)
            assert(svg.children().length.toInt > 0)
          case x             => sys.error(s"Whoa unexpected shape type: $x")
        }
    }
    println(svg.node_Svg.outerHTML)
  }
}
