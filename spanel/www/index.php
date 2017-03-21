<?php
define("PHP_ROOT", $_SERVER['DOCUMENT_ROOT'] . '/../php');
require_once PHP_ROOT . '/me/fru1t/common/language/Autoload.php';
use me\fru1t\common\language\Autoload;
use me\fru1t\common\language\Http;
use me\fru1t\common\language\Session;
use me\fru1t\common\router\Route;
use me\fru1t\common\router\Router;
use me\fru1t\common\template\Templates;

Autoload::setup(PHP_ROOT);

Session::setup("citatsia-php-session");

Router::setup()
    ->setContentDirectory('../php/me/fru1t/spanel/content')
    ->setDefaultContentPagePath('home.php')
    ->setErrorPagePath('home.php')
    ->setPageParameterName(Router::DEFAULT_PAGE_PARAMETER_NAME)
    ->map(Route::create('styles.css', '../styles/bin/styles.css', Http::HEADER_CONTENT_TYPE_CSS))
    ->map(Route::create('styles.css.map', '../styles/bin/styles.css.map'))
    ->complete();
Templates::setup()->complete();

Router::route();
