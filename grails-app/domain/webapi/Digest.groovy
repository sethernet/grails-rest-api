package webapi

class Digest {
    String input
    String output

    static belongsTo = [customer : Person]

    static constraints = {
        input(blank: false)
        output(nullable: true)
    }
}
