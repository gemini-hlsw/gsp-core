// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gem.enum

import gsp.math.{ Lat, Place }
import monocle.Getter

package object implicits {
  implicit class SiteOps(val site: Site) extends AnyVal {
    def toPlace: Place =
      Place(
        Lat.fromAngleWithCarry(site.latitude)._1,
        site.longitude,
        site.altitude.toDouble,
        site.timezone
      )
  }

  private val sitePlaceGetter: Getter[Site, Place] = Getter(_.toPlace)

  implicit class SiteModuleOps(val siteModule: Site.type) extends AnyVal {
    def place: Getter[Site, Place] = sitePlaceGetter
  }
}
