package br.com.bundherr.entities


import br.com.bundherr.PGDriver.simple._
import scala.collection.immutable.StringOps


/**
 * Created by Rafael on 26/12/13.
 */

object Entities {

  class AccountType(val code:Int)
  case object Asset extends AccountType(1)
  case object Liability extends AccountType(2)

  implicit val acctTypeMapper = MappedTypeMapper.base[AccountType, Int](_.code, new AccountType(_))

  case class StandardAccount(code:Option[Long],acctSet:Option[Int],name:String,acctType: AccountType,parentCode:Option[Long],parentSet:Option[Int])

  case class AccountSet(id:Option[Int],name:String,owner:Option[Long],format:String) extends NumberFormatter{
    base =  (format.split("#")(0)).toInt
    finalFormat =  (format.split("#")(1))
  }

  case class IdHolder(sequenz:Option[Int],name:String,format:String) extends NumberFormatter {
    base =  (format.split("#")(0)).toInt
    finalFormat =  (format.split("#")(1))
  }

  case class Institution(id:Option[Long],localId:Long,idHolder:Option[Int],name:String)

  trait NumberFormatter {
    var base:Int = 10
    var finalFormat:String = "0.0"
    lazy val digits:Int = finalFormat.count(_=='0')

    def format(code:BigInt)={
      val str = code.toString(base)
      val buffer = new StringBuilder
      def iterator(onStr:String,number:String){
        if(!onStr.isEmpty) {
          val v: Char = onStr.charAt(0)
          v match {
            case '0' => {buffer.append(if(number.length > 0)number.charAt(0) else '0'); iterator(onStr.drop(1),number.drop(1))}
            case _ => {buffer.append(v) ; iterator(onStr.drop(1),number)}
          }
        }
      }
      iterator(finalFormat.reverse,str.reverse)
      buffer.toString().reverse
    }

    def read(str:String) = BigInt(str.replaceAll("[^a-zA-Z\\d]",""),base)

  }
  abstract class SchemaTable[T](schema:String, name:String) extends Table[T](Option(schema),name)

  class IdSpaces extends Table[IdHolder](Some("MAIN"),"IDENTIFIER_SPACE"){
    def sequenz = column[Option[Int]]("SEQUENCE_NR",O.PrimaryKey,O.AutoInc)
    def name = column[String]("NAME")
    def format = column[String]("FORMAT")

    def * = sequenz ~ name ~ format <> (IdHolder,IdHolder.unapply _)

    def forInsert = name ~ format <> (
      { t => IdHolder(None,t._1 , t._2  ) },
      { (idsp: IdHolder) => Some((idsp.name , idsp.format)) }
      )
  }

  class Institutions extends Table[Institution](Some("MAIN"),"INSTITUTIONS"){
    def id = column[Option[Long]]("ID",O.PrimaryKey,O.AutoInc)
    def name = column[String]("NAME")
    def localId = column[Long]("local_id")
    def idHolder = column[Option[Int]]("id_holder")

    def fkIdSpace = foreignKey("FK_IDSPACE",idHolder,new IdSpaces)(_.sequenz)
    def * = id ~ localId ~ idHolder ~ name <> (Institution,Institution.unapply _)

    def forInsert =  localId ~ idHolder ~ name  <> (
        { t => Institution(None,t._1 , t._2 , t._3 ) },
        { (stdacct: Institution) => Some((stdacct.localId , stdacct.idHolder , stdacct.name )) }
     )
  }

  class AccountSets extends Table[AccountSet](Some("MAIN"),"ACCOUNTS_SET"){
    def id = column[Option[Int]]("ID",O.PrimaryKey,O.AutoInc)
    def name = column[String]("NAME")
    def owner = column[Option[Long]]("OWNER")
    def format = column[String]("FORMAT")

    def acctSetOwner = foreignKey("FK_ACCT_SET_OWNER",owner,new Institutions)(_.id)
    def * = id ~ name ~ owner ~ format <> (AccountSet,AccountSet.unapply _)

    def forInsert = name ~ owner ~ format <> (
      { t => AccountSet(None,t._1 , t._2 , t._3 ) },
      { (stdacct: AccountSet) => Some((stdacct.name , stdacct.owner , stdacct.format )) }
      )
  }


 class StandardAccounts extends Table[StandardAccount](Some("MAIN"),"STD_ACCOUNTS"){

    def code = column[Option[Long]]("CODE")
    def acctSet = column[Option[Int]]("ACCT_SET")
    def name = column[String]("NAME")
    def acctType = column[AccountType]("ACCT_TYPE")
    def parentCode = column[Option[Long]]("PARENT_CODE")
    def parentSet = column[Option[Int]]("PARENT_CODE")

    def fkAccountSet = foreignKey("FK_ACCOUNT_STD_SET",acctSet,new AccountSets)(t => t.id)
    def fkParentAcct = foreignKey("AUTO_FK_PARENT_ACCT",(parentCode,parentSet),this)(t => (t.code,t.acctSet))
    def pkStdAccounts = primaryKey("PK_STD_ACCOUNTS",(code,acctSet))
    def * = code ~ acctSet ~ name ~ acctType ~ parentCode ~ parentSet <> (StandardAccount,StandardAccount.unapply _ )
  }


}
//abstract class SchemaTable[T](schemaName : =>String,tableName:String) extends Table[T](Option(schemaName),tableName)

