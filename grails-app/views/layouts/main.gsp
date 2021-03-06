<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><g:layoutTitle default="mzExtract"/></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap.min.css')}" type="text/css">
             <link rel="stylesheet" href="${resource(dir: 'css', file: 'hint.min.css')}" type="text/css">
             <g:javascript library="jquery" />
            <script src="${resource(dir: 'js', file: 'd3.v3.min.js')}"></script>
            <script src="${resource(dir: 'js', file: 'dimple.v1.1.3.js')}"></script>
		<style>
			html { overflow: -moz-scrollbars-vertical; overflow-y: scroll; }
			body {
                            margin: 0px auto;
                            padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
                        }
			ul { list-style: none; }
                        .page-table {
                          margin: 5px 0;
                        }
                        .page-tr {
                          margin: 1px 0;
                        }
                        .page-left {
                          padding: 15px;
                          margin: 3px;
                          vertical-align: top;
                          min-width: 250px;
                        }
                        .page-right {
                          padding: 15px;
                          margin: 3px;
                          border-left: thin solid #dcdcdc;
                          vertical-align: top;
                          min-width: 450px;
                        }
                        .btn {
                          margin-right: 3px;
                          margin-top: 1px;
                        }

                        g text.axis.title {
                           font-size: 12px !important;
                        }

                        g text.legendText {
                           font-size: 11px !important;
                        }

                        g.tick text {
                            font-size: 10px !important;
                        }

		</style>

<!--		<link rel="stylesheet" href="${resource(dir: 'css', file: 'bootstrap-responsive.min.css')}" type="text/css">-->
                <script src="${resource(dir: 'js', file: 'bootstrap.min.js')}"></script>


		<g:layoutHead/>
		<r:layoutResources />
	</head>
	<body>
	    <div class="navbar navbar-inverse navbar-fixed-top">
	      <div class="navbar-inner">
	        <div class="container">
	          <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
	            <span class="icon-bar"></span>
	            <span class="icon-bar"></span>
	            <span class="icon-bar"></span>
	          </button>
	          <g:link url="${resource(dir: '', file: '')}" class="brand"><em>mz</em><strong>Extract</strong></g:link>
	          <div class="nav-collapse collapse">
	            <ul class="nav">
	              <li class="active"><g:link controller="data" action="index">Data</g:link></li>
	            </ul>
	          </div><!--/.nav-collapse -->
	        </div>
	      </div>
	    </div>

	    <div class="container">
		<g:layoutBody/>
            </div> <!-- /container -->
            <script src="${resource(dir: 'js', file: 'bootstrap.min.js')}"></script>
            <g:javascript library="application"/>
            <r:layoutResources />
	</body>
</html>
