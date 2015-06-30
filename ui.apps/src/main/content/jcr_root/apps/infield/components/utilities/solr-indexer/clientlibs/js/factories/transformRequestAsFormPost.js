// I provide a request-transformation method that is used to prepare the outgoing
// request as a FORM post instead of a JSON packet.
InfieldCommonsApp.factory( "transformRequestAsFormPost", function() {

        function transformRequest( obj ) {

            var str = [];
            for ( var p in obj ) {

                str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
            }

            return str.join("&");
        }

        return transformRequest;

    }
);