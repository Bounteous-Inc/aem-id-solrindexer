InfieldCommonsApp.service("solrService", ["$q", "$http", "transformRequestAsFormPost",
    function($q, $http, transformRequestAsFormPost) {

    var servletURL = "/bin/services/infield/solrindex";

    /**
     * fetchData
     * Sends an ajax request to fetch the new solr index
     * @param {string} indexPath
     * @param {string} path
     * @param {string} aemUser
     * @param {string} aemPassword
     * @return {object} promise
     */
    this.fetchData = function(indexPath, path, aemUser, aemPassword) {

        var deferred = $q.defer();

        $http({
            method: 'GET',
            url: servletURL,
            params: {
                'indexPath': indexPath,
                'path': path,
                'aemUser': aemUser,
                'aemPassword': aemPassword
            },
            headers: {
                'Content-Type': 'application/json;charset=UTF-8',
                "Accept": "application/json;charset=utf-8"
            },
            timeout: 30000, // timeout abort AJAX
            cache: false
        })
        .success(function(data) {

             deferred.resolve(data);
         })
        .error(function() {

            deferred.reject();
        });

        return deferred.promise;
    };

    /**
     * deleteIndex
     * Sends an ajax request to delete the current solr index
     * @param {string} solrurl
     * @param {string} indexPath
     * @return {object} promise
     */
    this.deleteIndex = function(solrurl, indexPath) {

        var deferred = $q.defer();

        $http
            .delete(servletURL, {
                params: {
                    solrurl: solrurl,
                    indexPath: indexPath
                }
            })
            .success(function(data) {

                deferred.resolve(data);
            })
            .error(function() {

                deferred.reject();
            });

        return deferred.promise;
    };

    /**
     * updateIndex
     * Sends an ajax request to update the solr index
     * @param {string} solrurl
     * @param {string} content
     * @return {object} promise
     */
    this.updateIndex = function(solrurl, content) {

        var deferred = $q.defer();

        $http
            .post(servletURL, {
                solrurl: solrurl,
                content: content
            }, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                transformRequest: transformRequestAsFormPost
            })
            .success(function(data) {

                deferred.resolve(data);
            })
            .error(function() {

                deferred.reject();
            });

        return deferred.promise;
    };
}]);
