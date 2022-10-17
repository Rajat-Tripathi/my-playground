package others

import others.InstantMessagingApp.{Contact, Tag}

import scala.collection.mutable.ListBuffer


/**
  * Created by Rajat on Sep 29, 2022.
  */

object InstantMessagingApp {
  sealed trait Tag

  sealed case class Contact(name: String, number: Long, tags: Seq[Tag]) {

    def hasTag(tag: Tag): Boolean = this.tags.contains(tag)

    def hasAtLeastOneTag(tags: Seq[Tag]): Boolean = {
      tags.exists(t => hasTag(t))
    }

    def hasAllTags(tags: Seq[Tag]): Boolean = {
      tags.forall(t => hasTag(t))
    }
  }

  sealed case class Story(content: Any, visibleToTags: Seq[Tag]) {
    def toPublish(contacts: Seq[Contact]): StoryPublish = {
      StoryPublish(content, contacts.map(_.number))
    }
  }

  sealed case class StoryPublish(content: Any, visibleToNumbers: Seq[Long]) extends Serializable
}

abstract class InstantMessagingApp {

  import InstantMessagingApp._

  def tags: Seq[Tag]

  def contacts: Seq[Contact]

  def addTag(tag: Tag): Seq[Tag]

  def removeTag(tag: Tag): Seq[Tag]

  def addContact(contact: Contact): Seq[Contact]

  def removeContact(contact: Contact): Seq[Contact]

  private def publishStory(story: Story, contacts: Seq[Contact]): Unit = {
    if (contacts.nonEmpty) {
      val publish = story.toPublish(contacts)
      println("publishing " + publish)
      //add publish logic here
    }
  }

  def shareStory(story: Story): Unit = {
    val filteredContacts = contacts.filter(u => u.hasAtLeastOneTag(story.visibleToTags))
    publishStory(story, filteredContacts)
  }
}

private object LocalStorage {

  private val tags: ListBuffer[Tag] = new scala.collection.mutable.ListBuffer[Tag]

  def getTags: Seq[Tag] = tags

  def addTag(tag: Tag): Seq[Tag] = tags.+=(tag)

  def removeTag(tag: Tag): Seq[Tag] = tags.-(tag)

  private val contacts: ListBuffer[Contact] = new scala.collection.mutable.ListBuffer[Contact]

  def getContacts: Seq[Contact] = contacts

  def addContact(contact: Contact): Seq[Contact] = contacts.+=(contact)

  def removeContact(contact: Contact): Seq[Contact] = contacts.-(contact)
}

object MyAccount extends InstantMessagingApp with App {

  import InstantMessagingApp._

  case object Friends extends Tag

  case object CloseFriends extends Tag

  case object Colleagues extends Tag

  case object Family extends Tag

  //  def tags: Seq[Tag] = Seq(Friends, CloseFriends,Colleagues,Family)
  //
  //  def contacts: Seq[Contact] = List(
  //    Contact("abc1", 123L, Nil),
  //    Contact("abc2", 123L, Seq(Friends)),
  //    Contact("abc3", 123L, Seq(Family, CloseFriends)),
  //    Contact("abc4", 123L, Seq(Colleagues))
  //  )

  def tags: Seq[Tag] = LocalStorage.getTags

  def contacts: Seq[Contact] = LocalStorage.getContacts

  def addTag(tag: Tag): Seq[Tag] = LocalStorage.addTag(tag)

  def removeTag(tag: Tag): Seq[Tag] = LocalStorage.removeTag(tag)

  def addContact(contact: Contact): Seq[Contact] = LocalStorage.addContact(contact)

  def removeContact(contact: Contact): Seq[Contact] = LocalStorage.removeContact(contact)

}