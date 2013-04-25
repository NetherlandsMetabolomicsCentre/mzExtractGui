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
		<style>
			html { overflow: -moz-scrollbars-vertical; overflow-y: scroll; }
			body { padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */ }
			ul { list-style: none; }
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
	          <g:link controller="do" action="home" class="brand"><em>mz</em><strong>Extract</strong></g:link>
	          <div class="nav-collapse collapse">
	            <ul class="nav">
	              <li class="active"><g:link controller="do" action="home">Home</g:link></li>
	              <li><g:link controller="do" action="index">Projects</g:link></li>
	              <li><g:link controller="do" action="help">Help</g:link></li>
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
