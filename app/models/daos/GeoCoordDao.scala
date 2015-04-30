package models.daos

import java.util.UUID
import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import scala.concurrent.Future
import models.GeoCoord
import models.GeoCoord
import models.NamedLocation
import org.joda.time.Interval

trait GeoCoordDao {

  def save(geoCoord: GeoCoord, apiKey: UUID): GeoCoord

  def load(user: User): List[GeoCoord]

  def loadLatest(apiKey: UUID): Option[GeoCoord]

  def findMatchingCoordinates(user: User, location: NamedLocation, interval: Interval): List[GeoCoord]

}
