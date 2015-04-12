package models

import java.util.UUID
import org.joda.time.DateTime

case class GeoCoord(
  id: Option[Long],
  userId: UUID,
  latitude: Double,
  longitude: Double,
  altitude: Double,
  accuracy: Float,
  speed: Float,
  time: DateTime
)
