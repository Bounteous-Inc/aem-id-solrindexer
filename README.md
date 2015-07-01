# Infield Solr Indexer

This project provides a nice UI on top of Headwire's [AEM Solr Search](https://github.com/headwirecom/aem-solr-search). It's essentially a replacement of the shell script that comes with Headwire's code. In that way, authors have an easy way to bulk index parts of the AEM site tree.  
It has the following features:

* Easy to use for authors.
* Based on new Coral UI and Angular JS.
* (Almost) no set up needed (it pulls all properties from the OSGi config)

## Requirements

* Headwire's [AEM Solr Search](https://github.com/headwirecom/aem-solr-search)
* Custom Bulk Indexer (as outlined [here](https://github.com/headwirecom/aem-solr-search/blob/master/aemsolrsearch-geometrixx-media-sample/src/main/java/com/headwire/aemsolrsearch/geometrixxmedia/servlets/SolrBulkUpdateHandler.java)) that can take a "path" parameter and creates the index based on that.
* Tested in AEM 6.0 SP2 but it should work with older versions as long as the 'coralui2' clientlib exists

## Screenshots

![Screenshot set up](https://raw.githubusercontent.com/infielddesign/aem-id-solrindexer/master/screenshot_init.png "Set up screen")
![Screenshot process](https://raw.githubusercontent.com/infielddesign/aem-id-solrindexer/master/screenshot_done.png "Process")


## Questions & Feedback

Contact [Infield Design](http://www.infielddesign.com/contact/) or create an issue here in GitHub.
