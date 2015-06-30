InfieldCommonsApp.service("progressService", function() {

    this.data = {};

    /**
     * add
     * Adds a new progress information
     * @param {string} key
     * @param {string} message
     * @return void
     */
    this.add = function(key, message) {

        if ( !this.data[key] ) {

            this.data[key] = {
                "message": message,
                "status": "running"
            };
        }
    };

    /**
     * update
     * Updates progress information
     * @param {string} key
     * @param {string} message
     * @param {string} status
     * @return void
     */
    this.update = function(key, message, status) {

         if ( this.data[key] ) {

             if ( typeof message !== undefined ) {

                 this.data[key].message = message;
             }

            if ( typeof status !== undefined ) {

                this.data[key].status = status;
             }
         }
    };

    /**
     * done
     * Shorthand for update(key, message, "done")
     * @param {string} key
     * @param {string} message
     * @return void
     */
    this.done = function(key, message) {

        if ( this.data[key] ) {

            this.update(key, message, "done");
        }
    };

    /**
     * error
     * Shorthand for update(key, message, "error")
     * @param {string} key
     * @param {string} message
     * @return void
     */
    this.error = function(key, message) {

        if ( this.data[key] ) {

            this.update(key, message, "error");
        }
    };

    /**
     * reset
     * Reset the progress data
     * @return void
     */
    this.reset = function() {

        for ( var i in this.data ) {

            delete this.data[i];
        }
    };
});