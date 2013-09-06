package ru.zconstz.simuctor

import akka.actor.{ActorRef, Actor}
import scala.collection.mutable

trait WorldObject

case object Rock extends WorldObject

case object Pit extends WorldObject

trait ActiveWorldObject extends WorldObject {
  def actor: ActorRef
}

sealed trait WorldMessage

case class MoveObject(from: (Int, Int), to: (Int, Int)) extends WorldMessage

case class RemoveObject(at: (Int, Int)) extends WorldMessage

case class AddObject(obj: WorldObject, at: (Int, Int)) extends WorldMessage

case object GetWorldStateAsString extends WorldMessage

case class WorldStateAsString(str: String)

class World extends Actor {
  val objects: mutable.Map[(Int, Int), WorldObject] = new mutable.HashMap[(Int, Int), WorldObject]()

  def receive = {
    case AddObject(obj, at) => objects += ((at, obj))
    case RemoveObject(at) => objects -= at
    case MoveObject(from, to) => for (obj <- objects.remove(from)) objects += ((to, obj))
    case GetWorldStateAsString => sender ! WorldStateAsString(objects.toString())
  }
}

