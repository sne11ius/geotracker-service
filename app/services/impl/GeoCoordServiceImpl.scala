package services.impl

import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import scala.concurrent.Future
import javax.inject.Inject
import models.GeoCoord
import models.daos.GeoCoordDao
import services.GeoCoordService
import java.util.UUID
import models.User
import models.NamedLocation
import org.joda.time.Interval
import play.api.Play.current
import org.joda.time.DateTime
import scala.collection.mutable.MutableList
import org.joda.time.Duration
import play.api.Logger

class GeoCoordServiceImpl @Inject() (geoCoordDao: GeoCoordDao) extends GeoCoordService {

  def save(geoCoord: GeoCoord, apiKey: UUID): GeoCoord = {
    geoCoordDao.save(geoCoord, apiKey)
  }

  def save(geoCoords: Seq[GeoCoord], apiKey: UUID) {
    geoCoords.map { c => geoCoordDao.save(c, apiKey) }
  }

  def load(user: User): List[GeoCoord] = {
    geoCoordDao.load(user)
  }

  def loadLatest(apiKey: UUID): Option[GeoCoord] = {
    geoCoordDao.loadLatest(apiKey)
  }

  override def findMatchingCoordinates(user: User, location: NamedLocation, interval: Interval): List[GeoCoord] = {
    geoCoordDao.findMatchingCoordinates(user, location, interval)
  }

  def pad(d: DateTime): DateTime = {
    // if (num % padding > padding / 2) num + (padding - (num % padding)) else num - (num % padding)
    val padding = current.configuration.getInt("calendarPadding").getOrElse(15)
    val oldMinutes = d.getMinuteOfHour
    val mod = oldMinutes % padding
    val newMinutes = if (mod > padding / 2) oldMinutes + (padding - mod) else oldMinutes - mod
    if (0 > newMinutes || 59 < newMinutes) {
      Logger.warn(s"Problematic minutes: $newMinutes")
    }
    if (60 == newMinutes) {
      d.plusHours(1).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)
    } else {
      d.withMinuteOfHour(newMinutes).withSecondOfMinute(0).withMillisOfSecond(0)
    }
  }

  override def findMatchingIntervals(user: User, location: NamedLocation, interval: Interval): List[Interval] = {
    val minExitMinutes = current.configuration.getInt("minExitMinutes").getOrElse(15)

    val coords = findMatchingCoordinates(user, location, interval)
    // TODO
    // 1. Put coords in a List[List[GeoCoord]] according to the respective times and minExitMinutes
    // 2. Make this a List[Interval]
    // 3. Pad the intervals
    // 4. Remove zero-length intervals
    if (coords.isEmpty) {
      List()
    } else if (1 == coords.length) {
      List(new Interval(pad(coords.head.time), pad(coords.head.time)))
    } else if (2 == coords.length) {
      List(new Interval(pad(coords.head.time), pad(coords.drop(1).head.time)))
    } else {
      var buckets = MutableList[MutableList[GeoCoord]]()
      var lastCoord = coords.head
      var currentList = MutableList[GeoCoord]()
      currentList += lastCoord
      buckets += currentList
      for (c <- coords) {
        val duration = new Duration(lastCoord.time, c.time)
        if (duration.getStandardMinutes > minExitMinutes) {
          currentList = MutableList[GeoCoord]()
          buckets += currentList
        }
      	currentList += c
        lastCoord = c
      }
      buckets.map {
        _.sortBy { _.time.getMillis }
      }.map {
        l => new Interval(pad(l.head.time), pad(l.last.time))
      }.filter {
        0 < _.toDurationMillis
      }.toList
    }
  }
}
