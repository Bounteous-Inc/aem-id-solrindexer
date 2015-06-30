/**
 * SolrIndexCtrl
 *
 * Controller to update the Solr index
 */

InfieldCommonsApp.controller("SolrIndexCtrl",
    ["$scope", "$timeout", "progressService", "solrService",
    function($scope, $timeout, progressService, solrService) {

    $scope.preventInput = false;
    $scope.finished = false;
    $scope.progress = progressService.data;
    $scope.indexContent = "";

    /**
     * collectData
     * Send an ajax request to generate new json files with the new index
     * @return void
     */
    $scope.collectData = function() {

        // Prevent any changes to the form
        $scope.preventInput = true;
        $scope.finished = false;

        var progressIndex = "collectData";

        progressService.add(progressIndex, "Collecting data for Solr index.");

        var fetchPromise = solrService.fetchData(
            $scope.config.indexPath,
            $scope.config.path,
            $scope.config.aemUser,
            $scope.config.aemPassword
        );

        fetchPromise.then(function(data) {

            if ( data.success ) {
                progressService.done(progressIndex, data.message);
                $scope.indexContent = data.content;
                $scope.deleteIndex();
            } else {
                progressService.error(progressIndex, data.message);
            }

            // if an error occurred we stop here:
            if (data.success == false) {
                $scope.finished = true;
            }

        }, function(data) {

            progressService.error(progressIndex, "An error occurred while collecting the data. Please make sure the credentials and connection URL are correct.");
            $scope.finished = true;

        });
    };

    /**
     * deleteIndex
     * Sends an ajax request to delete the current solr index
     * @return void
     */
    $scope.deleteIndex = function() {

        var progressIndex = "deleteIndex";

        progressService.add(progressIndex, "Deleting Solr index.");

        var deletePromise = solrService.deleteIndex(
            $scope.config.solrurl,
            $scope.config.indexPath
        );

        deletePromise.then(function(data) {

            if ( data.success ) {
                progressService.done(progressIndex, data.message);

                $scope.updateIndex();
            } else {

                progressService.error(progressIndex, data.message);
            }

            // if an error occurred we stop here:
            if (data.success == false) {
                $scope.finished = true;
            }

        }, function(data) {

            progressService.error(progressIndex, "An error occurred while deleting the current Solr index. Please make sure the credentials and connection URL are correct.");
            $scope.finished = true;

        });
    };

    /**
     * updateIndex
     * Sends an ajax request to update the solr index
     * @return void
     */
    $scope.updateIndex = function() {

        var progressIndex = "updateIndex";

        progressService.add(progressIndex, "Updating Solr index.");

        var updatePromise = solrService.updateIndex(
            $scope.config.solrurl,
            $scope.indexContent
        );

        updatePromise.then(function(data) {

            if ( data.success ) {

                progressService.done(progressIndex, data.message);
            } else {

                progressService.error(progressIndex, data.message);
            }

            // in any case, show the re-do and change buttons:
            $scope.finished = true;

        }, function(data) {

            progressService.error(progressIndex, "An error occurred while updating the index. Please make sure the credentials and connection URL are correct.");
            $scope.finished = true;

        });
    };

    /**
     * runAgain
     * Calls the collectData method
     * @return void
     */
    $scope.runAgain = function() {

        progressService.reset();
        $scope.indexContent = "";
        $scope.collectData();
    };

    /**
     * changeValues
     * Remove the disabled attribute from the form
     * @return void
     */
    $scope.changeValues = function() {

        progressService.reset();
        $scope.indexContent = "";
        $scope.preventInput = false;
    };

    /**
     * noProgress
     * Evaluates whether there are progress items or not
     * @param {object} data
     * @return {boolean}
     */
    $scope.noProgress = function(data) {

        return angular.equals({}, data);
    };
}]);
