package services

import models.GeoCoord
import java.util.UUID
import models.User
import models.NamedLocation
import org.joda.time.Interval

trait GeoCoordService {

  def save(geoCoord: GeoCoord, apiKey: UUID): GeoCoord

  def save(geoCoords: Seq[GeoCoord], apiKey: UUID)

  def loadAll(user: User): List[GeoCoord]

  def loadLatest(apiKey: UUID): Option[GeoCoord]

  def load(user: User, interval: Interval): List[GeoCoord]

  def findMatchingCoordinates(user: User, location: NamedLocation, interval: Interval): List[GeoCoord]

  def findMatchingIntervals(user: User, location: NamedLocation, interval: Interval): List[Interval]

}
