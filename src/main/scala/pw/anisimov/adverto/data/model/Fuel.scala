package pw.anisimov.adverto.data.model

sealed trait Fuel

case object Gasoline extends Fuel

case object Diesel extends Fuel
