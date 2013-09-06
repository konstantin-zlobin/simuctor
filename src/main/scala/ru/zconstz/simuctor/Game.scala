package ru.zconstz.simuctor

import akka.actor.{ActorRef, ActorDSL, Actor}
import akka.dispatch.Dispatchers

sealed trait GameMessage

case object InitGame extends GameMessage

case object FinishGame extends GameMessage

case object GameTick extends GameMessage

case object GameInitialized

class Game extends Actor {

  var worldOpt: Option[ActorRef] = None

  val activeObjects = new scala.collection.mutable.ArrayBuffer[ActorRef]()

  def receive = {
    case InitGame => initGame()

    case FinishGame => {
      worldOpt = None
      for (activeObject <- activeObjects) activeObject ! FinishGame
      activeObjects.clear()
    }

    case GameTick => tick()

    case WorldStateAsString(worldState) => println(worldState)
  }

  def initGame() {
    worldOpt = Some(ActorDSL.actor(new World))
    val world = worldOpt.get

    def randomInRange(multiplier: Int): Int = (math.random * multiplier).toInt

    val worldBoundaries = (20, 20)

    for (i <- 1 to 100) world ! AddObject(Rock, (randomInRange(worldBoundaries._1), randomInRange(worldBoundaries._2)))
    for (i <- 1 to 50) world ! AddObject(Pit, (randomInRange(worldBoundaries._1), randomInRange(worldBoundaries._2)))

    //for (i <- 1 to 10) world ! AddObject( )

    world ! GetWorldStateAsString

    sender ! GameInitialized
  }

  def tick() = for (world <- worldOpt) {
    println("Game: tick received")
    for (liveObject <- activeObjects) {
      liveObject ! GameTick
    }
  }
}