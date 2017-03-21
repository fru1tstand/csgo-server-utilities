<?php
namespace me\fru1t\spanel\content;
use me\fru1t\spanel\template\EmptyPage;

$body = <<<HTML
Hi
HTML;

EmptyPage::start()
    ->with(EmptyPage::FIELD_HTML_TITLE, "Fru1t.Me CSGO Server Panel - Home")
    ->with(EmptyPage::FIELD_BODY, $body)
    ->render();
