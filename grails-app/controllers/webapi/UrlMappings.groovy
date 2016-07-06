package webapi

class UrlMappings {

    static mappings = {

      "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        '/register'(controller: 'register') {
            action = [POST: 'save']
            format = 'json'
        }

        '/profile'(controller: 'profile') {
            action = [GET: 'index']
        }

        '/digests'(controller: 'digest') {
            action = [POST: 'save']
            format = 'json'
        }

        '/me/digests'(controller: 'me') {
            action = [GET: 'digests']
        }

        "/me/digests/$size?/$from?"(controller: 'me') {
            action = [GET: 'digests']
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }


}
