package jlo.ioe.command

abstract class CommandMsg

object CommandProcessor {
  var recordedCommands = List()
  
  // load commands
  // index them
  // also needs access to data store system! where the objects are indexed...
  loadAndIndexVocabulary
  
  def loadAndIndexVocabulary = {
    
  }

  def suggest(c : Command, t : String) : List[CommandPart] = {
    List[CommandPart]()
  }

  def execute(c : Command) = {
    
  }
}
