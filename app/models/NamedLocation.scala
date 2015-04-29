package models

case class NamedLocation(
  id: Option[Long],
  name: String,
  latitude: Double,
  longitude: Double,
  radius: Float
)
