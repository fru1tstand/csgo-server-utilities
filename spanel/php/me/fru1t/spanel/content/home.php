<?php
namespace me\fru1t\spanel\content;
use me\fru1t\spanel\template\EmptyDashboard;

$body = <<<HTML
<section>
  <div class="page-title">This is a title</div>
  <div class="section-title">This is a section title</div>
  <p>This is regular text</p>
  <a href="#">Link for the Lazy</a>
  
</section>
<section>
  <div class="page-title">This is a title</div>
  <div class="section-title">This is a section title</div>
  <p>This is regular text</p>
  <a href="#">Link for the Lazy</a>
  
</section>

HTML;

EmptyDashboard::start()
    ->with(EmptyDashboard::FIELD_TITLE, "Fru1t.Me CSGO Server Panel - Home")
    ->with(EmptyDashboard::FIELD_CONTENT, $body)
    ->render();
