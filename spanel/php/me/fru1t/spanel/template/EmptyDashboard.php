<?php
namespace me\fru1t\spanel\template;
use me\fru1t\common\template\Template;
use me\fru1t\common\template\TemplateField;

/**
 *
 */
class EmptyDashboard extends Template {
  public const FIELD_TITLE = 'title';
  public const FIELD_CONTENT = 'content';

  /**
   * Produces the content this template defines in the form of an HTML string. This method is passed
   * a map with template field names as keys, and values that the content page provides.
   * @param string[] $fields An associative array mapping fields to ContentField objects.
   * @return string
   */
  public static function getTemplateRenderContents_internal(array $fields): string {
    $body = <<<HTML
<div class="dash-header">
  <div class="v-aligner"></div><select>
    <option>[fru1t.me] Minigames + Funmaps | 128 Tick | FastDL | Seattle</option>
  </select>
</div>
<div class="dash-body">
  <div class="dash-sidebar">
    <ul class="dash-sidebar-links">
      <li class="divider">Basic Server</li>
      <li><a href="#">Overview</a></li>
      <li><a href="#">Element</a></li>
      <li><a href="#">Element</a></li>
      <li><a href="#">Element</a></li>
      <li><a href="#">Element</a></li>
      <li><a href="#">Element</a></li>
    </ul>
    <ul class="dash-sidebar-links">
      <li class="divider">Section</li>
      <li><a href="#">Element</a></li>
      <li><a href="#">Element</a></li>
      <li><a href="#">Element</a></li>
      <li><a href="#">Element</a></li>
      <li><a href="#">Element</a></li>
      <li><a href="#">Element</a></li>
    </ul>
  </div>
  <div class="dash-content">
    {$fields[self::FIELD_CONTENT]}
  </div>
</div>
HTML;
    return EmptyPage::start()
        ->with(EmptyPage::FIELD_HTML_TITLE, $fields[self::FIELD_TITLE])
        ->with(EmptyPage::FIELD_BODY, $body)
        ->render(false, true);
  }

  /**
   * Provides the fields this template contains. Return null or an empty array to signal no fields.
   * @return null|TemplateField[]
   */
  static function getTemplateFields_internal(): ?array {
    return TemplateField::createFrom(self::FIELD_TITLE, self::FIELD_CONTENT);
  }
}
