package webapi.marshallers

import org.grails.web.converters.marshaller.ClosureObjectMarshaller
import webapi.Digest

class DigestMarshallerJson extends ClosureObjectMarshaller<Digest> {

    public static marshal = { Digest digest ->
        def json = [:]
        json.input = digest.input
        json.output = digest.output
        json
    }

    public DigestMarshallerJson() {
        super(Digest, marshal)
    }
}
