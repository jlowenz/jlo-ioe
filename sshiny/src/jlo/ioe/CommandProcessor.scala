package jlo.ioe.command


abstract class CommandMsg

object CommandProcessor {
  var recordedCommands = List()

  def suggest(c : Command, t : String) : List[CommandPart] = {
    List[CommandPart]()
  }

  def execute(c : Command) = {
    
  }
}
