# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table academic_files (
  id                        bigint auto_increment not null,
  program_id                bigint,
  file_name                 varchar(255),
  unique_name               varchar(255),
  description               varchar(255),
  required                  tinyint(1) default 0,
  session_id                bigint,
  delete_status             tinyint(1) default 0,
  constraint pk_academic_files primary key (id))
;

create table academic_year (
  id                        bigint auto_increment not null,
  year_name                 varchar(255),
  status                    tinyint(1) default 0,
  delete_status             tinyint(1) default 0,
  constraint pk_academic_year primary key (id))
;

create table account (
  id                        bigint auto_increment not null,
  applicant_id              bigint,
  amount                    double,
  delete_status             tinyint(1) default 0,
  created_at                datetime,
  constraint pk_account primary key (id))
;

create table announce (
  id                        bigint auto_increment not null,
  title                     varchar(255),
  content                   varchar(255),
  date                      datetime,
  attachment                varchar(255),
  pub_date                  datetime,
  type                      varchar(255),
  category                  varchar(255),
  status                    varchar(255),
  delete_status             tinyint(1) default 0,
  constraint pk_announce primary key (id))
;

create table anounce_role (
  id                        bigint auto_increment not null,
  role_id                   bigint,
  announce_id               bigint,
  delete_status             tinyint(1) default 0,
  constraint pk_anounce_role primary key (id))
;

create table applicant (
  id                        bigint auto_increment not null,
  profile                   varchar(255),
  first_name                varchar(255),
  family_name               varchar(255),
  sponsor_instutute         varchar(255),
  shelf_number              varchar(255),
  comments                  varchar(255),
  state                     varchar(255),
  education_background      varchar(255),
  user_id                   bigint,
  country                   varchar(255),
  districts_id              bigint,
  chosen                    tinyint(1) default 0,
  marital_status            varchar(255),
  year_completion           integer,
  birth_date                varchar(255),
  birth_place               varchar(255),
  gender                    varchar(255),
  national_dentity          varchar(255),
  nationality               varchar(255),
  sponsor                   varchar(255),
  sponsor_phone             varchar(255),
  a_school                  varchar(255),
  experience_draft          integer,
  experience                integer,
  mother_name               varchar(255),
  father_name               varchar(255),
  a_fromu                   integer,
  a_to                      integer,
  need_accomodation         tinyint(1) default 0,
  need_catering             tinyint(1) default 0,
  delete_status             tinyint(1) default 0,
  date                      datetime,
  active                    tinyint(1) default 0,
  constraint pk_applicant primary key (id))
;

create table applied (
  id                        bigint auto_increment not null,
  applicant_id              bigint,
  training_id               bigint,
  secondary_school          varchar(255),
  first_principal           varchar(255),
  second_principal          varchar(255),
  first_grade               varchar(255),
  second_grade              varchar(255),
  year                      integer,
  feespayment               varchar(255),
  sponsor_name              varchar(255),
  sponsor_phone             varchar(255),
  sponsor_email             varchar(255),
  statement                 varchar(255),
  contact_person            varchar(255),
  contact_email             varchar(255),
  contact_phone             varchar(255),
  disability                tinyint(1) default 0,
  disabilty_details         varchar(255),
  public_info               varchar(255),
  public_info_details       varchar(255),
  born_country              varchar(255),
  application_date          varchar(255),
  application_status        varchar(255),
  status_comment            varchar(255),
  shelf_number              varchar(255),
  shelfed                   tinyint(1) default 0,
  status                    tinyint(1) default 0,
  delete_status             tinyint(1) default 0,
  constraint pk_applied primary key (id))
;

create table assignment (
  id                        bigint auto_increment not null,
  assignment_title          varchar(255),
  description               varchar(255),
  attachment                varchar(255),
  start_date                datetime,
  end_date                  datetime,
  duration                  varchar(255),
  max                       float,
  types                     varchar(255),
  grouped                   tinyint(1) default 0,
  group_id                  bigint,
  component_id              bigint,
  lecture_id                bigint,
  training_id               bigint,
  delete_status             tinyint(1) default 0,
  constraint pk_assignment primary key (id))
;

create table assignment_result (
  id                        bigint auto_increment not null,
  result                    float,
  cliam_result              float,
  comment                   varchar(255),
  comment_lecrurer          varchar(255),
  assignment_id             bigint,
  student_id                bigint,
  done_by                   varchar(255),
  delete_status             tinyint(1) default 0,
  done_at                   datetime,
  constraint pk_assignment_result primary key (id))
;

create table attachment (
  id                        bigint auto_increment not null,
  attach_name               varchar(255),
  app_id                    bigint,
  file_id                   bigint,
  delete_status             tinyint(1) default 0,
  constraint pk_attachment primary key (id))
;

create table attendance (
  id                        bigint auto_increment not null,
  schedule_id               bigint,
  component_id              bigint,
  student_id                bigint,
  attended                  tinyint(1) default 0,
  attendedp                 tinyint(1) default 0,
  comment                   varchar(255),
  claimed                   tinyint(1) default 0,
  date                      datetime,
  delete_status             tinyint(1) default 0,
  constraint pk_attendance primary key (id))
;

create table bank_account (
  id                        bigint auto_increment not null,
  bank_name                 varchar(255),
  bank_acronym              varchar(255),
  open_branch               varchar(255),
  account_number            varchar(255),
  delete_status             tinyint(1) default 0,
  constraint pk_bank_account primary key (id))
;

create table block (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  acronym                   varchar(255),
  delete_status             tinyint(1) default 0,
  date                      datetime,
  constraint pk_block primary key (id))
;

create table campus (
  id                        bigint auto_increment not null,
  campus_name               varchar(255),
  campus_location           varchar(255),
  delete_status             tinyint(1) default 0,
  constraint pk_campus primary key (id))
;

create table campus_program (
  id                        bigint auto_increment not null,
  campus_id                 bigint,
  program_id                bigint,
  delete_status             tinyint(1) default 0,
  constraint pk_campus_program primary key (id))
;

create table campus_program_mode (
  id                        bigint auto_increment not null,
  campus_program_id         bigint,
  mode_id                   bigint,
  delete_status             tinyint(1) default 0,
  constraint pk_campus_program_mode primary key (id))
;

create table category (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  acronym                   varchar(255),
  description               text,
  group_id                  bigint,
  delete_status             tinyint(1) default 0,
  date                      datetime,
  constraint pk_category primary key (id))
;

create table chat_message (
  id                        bigint auto_increment not null,
  send_to_id                bigint,
  send_from_id              bigint,
  content                   varchar(255),
  type                      varchar(255),
  date                      datetime,
  message_read              tinyint(1) default 0,
  delete_status             tinyint(1) default 0,
  constraint pk_chat_message primary key (id))
;

create table checked (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  announce_id               bigint,
  delete_status             tinyint(1) default 0,
  constraint pk_checked primary key (id))
;

create table component (
  id                        bigint auto_increment not null,
  comp_name                 varchar(255),
  code                      varchar(255),
  credits                   integer,
  module_id                 bigint,
  delete_status             tinyint(1) default 0,
  constraint pk_component primary key (id))
;

create table component_max (
  id                        bigint auto_increment not null,
  component_id              bigint,
  research_max              float,
  exam_max                  float,
  lecture_id                bigint,
  training_id               bigint,
  constraint pk_component_max primary key (id))
;

create table country (
  id                        integer auto_increment not null,
  iso                       varchar(255),
  name                      varchar(255),
  nicename                  varchar(255),
  iso3                      varchar(255),
  numcode                   integer,
  phonecode                 integer,
  constraint pk_country primary key (id))
;

create table course_material (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  file_name                 varchar(255),
  schedule_id               bigint,
  delete_status             tinyint(1) default 0,
  date                      datetime,
  constraint pk_course_material primary key (id))
;

create table damage (
  id                        bigint auto_increment not null,
  student_id                bigint,
  name                      varchar(255),
  description               varchar(255),
  amount                    double,
  clear                     tinyint(1) default 0,
  delete_status             tinyint(1) default 0,
  date                      datetime,
  constraint pk_damage primary key (id))
;

create table date_range (
  id                        bigint auto_increment not null,
  start_date                datetime,
  end_date                  datetime,
  date                      datetime,
  delete_status             tinyint(1) default 0,
  constraint pk_date_range primary key (id))
;

create table day_session (
  id                        bigint auto_increment not null,
  day_id                    bigint,
  session_id                bigint,
  delete_status             tinyint(1) default 0,
  constraint pk_day_session primary key (id))
;

create table days (
  id                        bigint auto_increment not null,
  day_name                  varchar(255),
  day_number                integer,
  delete_status             tinyint(1) default 0,
  constraint pk_days primary key (id))
;

create table districts (
  id                        bigint auto_increment not null,
  district_name             varchar(255),
  provinces_id              bigint,
  constraint pk_districts primary key (id))
;

create table employee (
  id                        bigint auto_increment not null,
  employee_first_name       varchar(255),
  employee_last_name        varchar(255),
  gender                    varchar(255),
  employee_title            varchar(255),
  is_user                   tinyint(1) default 0,
  unit_id                   bigint,
  position_id               bigint,
  is_head_of_unit           tinyint(1) default 0,
  delete_status             tinyint(1) default 0,
  date                      datetime,
  constraint pk_employee primary key (id))
;

create table english_skills (
  id                        bigint auto_increment not null,
  skill_name                varchar(255),
  label_name                varchar(255),
  delete_status             tinyint(1) default 0,
  constraint pk_english_skills primary key (id))
;

create table ev_category (
  id                        bigint auto_increment not null,
  content                   varchar(255),
  constraint pk_ev_category primary key (id))
;

create table ev_question (
  id                        bigint auto_increment not null,
  category_id               bigint,
  question                  varchar(255),
  textarea                  tinyint(1) default 0,
  constraint pk_ev_question primary key (id))
;

create table evaluation (
  id                        bigint auto_increment not null,
  lecture_id                bigint,
  student_id                bigint,
  mark                      float,
  answer                    varchar(255),
  schedule_id               bigint,
  question_id               bigint,
  date                      datetime,
  constraint pk_evaluation primary key (id))
;

create table event (
  id                        bigint auto_increment not null,
  event_name                varchar(255),
  event_detail              varchar(255),
  date                      datetime,
  constraint pk_event primary key (id))
;

create table category_group (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  acronym                   varchar(255),
  description               text,
  delete_status             tinyint(1) default 0,
  date                      datetime,
  constraint pk_category_group primary key (id))
;

create table group_members (
  id                        bigint auto_increment not null,
  student_id                bigint,
  groups_id                 bigint,
  delete_status             tinyint(1) default 0,
  constraint pk_group_members primary key (id))
;

create table group_submission (
  id                        bigint auto_increment not null,
  groups_id                 bigint,
  assignment_id             bigint,
  student_id                bigint,
  comment                   varchar(255),
  attachment                varchar(255),
  is_marked                 tinyint(1) default 0,
  status                    varchar(255),
  date                      datetime,
  delete_status             tinyint(1) default 0,
  constraint pk_group_submission primary key (id))
;

create table groups (
  id                        bigint auto_increment not null,
  group_name                varchar(255),
  component_id              bigint,
  training_id               bigint,
  delete_status             tinyint(1) default 0,
  constraint pk_groups primary key (id))
;

create table hour_session (
  id                        bigint auto_increment not null,
  hour_id                   bigint,
  session_id                bigint,
  delete_status             tinyint(1) default 0,
  constraint pk_hour_session primary key (id))
;

create table hours (
  id                        bigint auto_increment not null,
  hour_name                 varchar(255),
  delete_status             tinyint(1) default 0,
  constraint pk_hours primary key (id))
;

create table ildplibrary (
  id                        bigint auto_increment not null,
  student_id                bigint,
  book_name                 varchar(255),
  book_number               varchar(255),
  book_cost                 float,
  act                       varchar(255),
  clear                     tinyint(1) default 0,
  delete_status             tinyint(1) default 0,
  constraint pk_ildplibrary primary key (id))
;

create table intake (
  id                        bigint auto_increment not null,
  intake_name               varchar(255),
  is_closed                 tinyint(1) default 0,
  year_id                   bigint,
  registration_fees         double,
  delete_status             tinyint(1) default 0,
  constraint pk_intake primary key (id))
;

create table intake_session_mode (
  id                        bigint auto_increment not null,
  intake_id                 bigint,
  session_mode_id           bigint,
  campus_program_id         bigint,
  delete_status             tinyint(1) default 0,
  constraint pk_intake_session_mode primary key (id))
;

create table internship (
  id                        bigint auto_increment not null,
  description               varchar(255),
  attachment                varchar(255),
  student_id                bigint,
  date                      datetime,
  constraint pk_internship primary key (id))
;

create table internship_appeal (
  id                        bigint auto_increment not null,
  reason                    varchar(255),
  attachment                varchar(255),
  comment_registrar         varchar(255),
  status                    varchar(255),
  app_type                  varchar(255),
  applicant_id              bigint,
  delete_status             tinyint(1) default 0,
  date                      datetime,
  constraint pk_internship_appeal primary key (id))
;

create table item (
  id                        bigint auto_increment not null,
  category_id               bigint,
  name                      varchar(255),
  min                       integer,
  max                       integer,
  unit_measure              varchar(255),
  unit_price                double,
  begining_qty              integer,
  purchase_qty              integer,
  consumption_qty           integer,
  balance_qty               integer,
  def_qnty                  integer,
  i_type                    varchar(255),
  removed                   varchar(255),
  origin                    varchar(255),
  allocation_place          varchar(255),
  depleciation_min_months   integer,
  status                    varchar(255),
  delete_status             tinyint(1) default 0,
  date                      datetime,
  constraint pk_item primary key (id))
;

create table lecture (
  id                        bigint auto_increment not null,
  f_name                    varchar(255),
  l_name                    varchar(255),
  qualification             varchar(255),
  specialization            varchar(255),
  done_by                   varchar(255),
  user_id                   bigint,
  delete_status             tinyint(1) default 0,
  constraint pk_lecture primary key (id))
;

create table location (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  block_id                  bigint,
  acronym                   varchar(255),
  delete_status             tinyint(1) default 0,
  date                      datetime,
  constraint pk_location primary key (id))
;

create table mark_sheet (
  id                        bigint auto_increment not null,
  student_id                bigint,
  module_id                 bigint,
  cat_result                integer,
  re_result                 integer,
  exam_result               integer,
  delete_status             tinyint(1) default 0,
  constraint pk_mark_sheet primary key (id))
;

create table module (
  id                        bigint auto_increment not null,
  module_name               varchar(255),
  module_code               varchar(255),
  credits                   integer,
  cat_max                   integer,
  exam_max                  integer,
  re_max                    integer,
  module_internship         tinyint(1) default 0,
  min_marks                 double,
  retake_amount             double,
  has_component             tinyint(1) default 0,
  lecture_id                bigint,
  program_id                bigint,
  delete_status             tinyint(1) default 0,
  constraint pk_module primary key (id))
;

create table payment (
  id                        bigint auto_increment not null,
  payment_type              varchar(255),
  account_id                bigint,
  bank_account_id           bigint,
  application_fees          double,
  fees_amount               double,
  accomodation_amount       double,
  restaurant_amount         double,
  retake_amount             double,
  remain                    double,
  slip_number               varchar(255),
  attachment                varchar(255),
  status                    varchar(255),
  comment                   varchar(255),
  delete_status             tinyint(1) default 0,
  date                      datetime,
  other_fees                double,
  done_by                   varchar(255),
  done_at                   datetime,
  constraint pk_payment primary key (id))
;

create table position (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  acronym                   varchar(255),
  description               text,
  delete_status             tinyint(1) default 0,
  date                      datetime,
  constraint pk_position primary key (id))
;

create table profile_info (
  id                        bigint auto_increment not null,
  profile_logo              varchar(255),
  profile_name              varchar(255),
  profile_acronym           varchar(255),
  email                     varchar(255),
  admission_email1          varchar(255),
  admission_email2          varchar(255),
  about_info                varchar(255),
  website                   varchar(255),
  registrar_signature       varchar(255),
  rector                    varchar(255),
  phone                     varchar(255),
  address                   varchar(255),
  default_text              varchar(255),
  cancel_text               varchar(255),
  min_age                   integer,
  attendance_percentage     integer,
  deliberation              integer,
  experience                integer,
  defer_text                varchar(255),
  fail_max                  float,
  pass_min                  float,
  pass_max                  float,
  credit_min                float,
  credit_max                float,
  distinction_min           float,
  distinction_max           float,
  score_one                 integer,
  score_two                 integer,
  score_three               integer,
  constraint pk_profile_info primary key (id))
;

create table program (
  id                        bigint auto_increment not null,
  cle                       tinyint(1) default 0,
  program_name              varchar(255),
  program_acronym           varchar(255),
  delete_status             tinyint(1) default 0,
  constraint pk_program primary key (id))
;

create table provinces (
  id                        bigint auto_increment not null,
  province_name             varchar(255),
  constraint pk_provinces primary key (id))
;

create table re_sit_re_take_request (
  id                        bigint auto_increment not null,
  request_type              varchar(255),
  status                    varchar(255),
  request_date              datetime,
  comment                   varchar(255),
  delete_status             tinyint(1) default 0,
  student_id                bigint,
  training_id               bigint,
  constraint pk_re_sit_re_take_request primary key (id))
;

create table refund (
  id                        bigint auto_increment not null,
  account_id                bigint,
  amount                    double,
  check_number              varchar(255),
  bank_account_id           bigint,
  refund_request_id         bigint,
  date                      datetime,
  delete_status             tinyint(1) default 0,
  constraint pk_refund primary key (id))
;

create table refund_request (
  id                        bigint auto_increment not null,
  account_id                bigint,
  amount                    double,
  attachment                varchar(255),
  details                   varchar(255),
  account_number            varchar(255),
  account_user_name         varchar(255),
  date                      datetime,
  status                    tinyint(1) default 0,
  delete_status             tinyint(1) default 0,
  constraint pk_refund_request primary key (id))
;

create table request (
  id                        bigint auto_increment not null,
  employee_id               bigint,
  status                    varchar(255),
  head_of_unit_status       tinyint(1) default 0,
  daf_status                tinyint(1) default 0,
  delete_status             tinyint(1) default 0,
  date                      datetime,
  constraint pk_request primary key (id))
;

create table request_details (
  id                        bigint auto_increment not null,
  request_type              varchar(255),
  status                    varchar(255),
  request_date              datetime,
  comment                   varchar(255),
  delete_status             tinyint(1) default 0,
  request_id                bigint,
  module_id                 bigint,
  constraint pk_request_details primary key (id))
;

create table roles (
  id                        bigint auto_increment not null,
  role_name                 varchar(255),
  session_name              varchar(255),
  is_allowed                tinyint(1) default 0,
  constraint pk_roles primary key (id))
;

create table room (
  id                        bigint auto_increment not null,
  room_name                 varchar(255),
  room_code                 varchar(255),
  campus_id                 bigint,
  flow_number               varchar(255),
  number_seat               integer,
  room_type                 varchar(255),
  done_by                   varchar(255),
  delete_status             tinyint(1) default 0,
  constraint pk_room primary key (id))
;

create table schedule (
  id                        bigint auto_increment not null,
  component_type            varchar(255),
  assignment                varchar(255),
  exam                      varchar(255),
  done_by                   varchar(255),
  delete_status             tinyint(1) default 0,
  lecture_id                bigint,
  component_id              bigint,
  room_id                   bigint,
  training_id               bigint,
  date_range_id             bigint,
  start_hour                varchar(255),
  end_hour                  varchar(255),
  date                      datetime,
  constraint pk_schedule primary key (id))
;

create table school_fees (
  id                        bigint auto_increment not null,
  fees_amount               integer,
  fees_details              varchar(255),
  intake_id                 bigint,
  delete_status             tinyint(1) default 0,
  program_mode_id           bigint,
  session_mode_id           bigint,
  constraint pk_school_fees primary key (id))
;

create table sector (
  id                        bigint auto_increment not null,
  sector_name               varchar(255),
  districts_id              bigint,
  constraint pk_sector primary key (id))
;

create table session (
  id                        bigint auto_increment not null,
  session_name              varchar(255),
  session_start             varchar(255),
  session_end               varchar(255),
  start_hour                varchar(255),
  end_hour                  varchar(255),
  delete_status             tinyint(1) default 0,
  constraint pk_session primary key (id))
;

create table session_mode (
  id                        bigint auto_increment not null,
  session_id                bigint,
  mode_id                   bigint,
  delete_status             tinyint(1) default 0,
  constraint pk_session_mode primary key (id))
;

create table source (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  description               text,
  delete_status             tinyint(1) default 0,
  date                      datetime,
  constraint pk_source primary key (id))
;

create table stock_movement (
  id                        bigint auto_increment not null,
  item_id                   bigint,
  request_id                bigint,
  proposed_qty              integer,
  confirmed_qty             integer,
  unit_price                double,
  source_fund               varchar(255),
  logistic_status           tinyint(1) default 0,
  logistic_comment          varchar(255),
  head_of_unit_status       tinyint(1) default 0,
  head_of_unit_comment      varchar(255),
  daf_status                tinyint(1) default 0,
  daf_comment               varchar(255),
  tag_number                varchar(255),
  serial_number             varchar(255),
  status                    tinyint(1) default 0,
  depleciation_year         integer,
  i_type                    varchar(255),
  from_location             bigint,
  to_location               bigint,
  location_id               bigint,
  delete_status             tinyint(1) default 0,
  done_by                   varchar(255),
  done_at                   datetime,
  date                      datetime,
  employee_status           tinyint(1) default 0,
  constraint pk_stock_movement primary key (id))
;

create table student (
  id                        bigint auto_increment not null,
  first_name                varchar(255),
  family_name               varchar(255),
  reg_no                    varchar(255),
  academic_email            varchar(255),
  applicant_id              bigint,
  training_id               bigint,
  status                    varchar(255),
  email_status              varchar(255),
  fail_count                integer,
  delete_status             tinyint(1) default 0,
  constraint pk_student primary key (id))
;

create table study_mode (
  id                        bigint auto_increment not null,
  mode_name                 varchar(255),
  mode_acronym              varchar(255),
  delete_status             tinyint(1) default 0,
  constraint pk_study_mode primary key (id))
;

create table sub_mark (
  id                        bigint auto_increment not null,
  student_id                bigint,
  component_id              bigint,
  cat_result                float,
  re_result                 float,
  exam_result               float,
  claim_cat                 float,
  claim_re                  float,
  claim_exam                float,
  comment                   varchar(255),
  delete_status             tinyint(1) default 0,
  done_by                   varchar(255),
  done_at                   datetime,
  constraint pk_sub_mark primary key (id))
;

create table submission (
  id                        bigint auto_increment not null,
  assignment_id             bigint,
  student_id                bigint,
  comment                   varchar(255),
  attachment                varchar(255),
  for_group                 tinyint(1) default 0,
  status                    varchar(255),
  date                      datetime,
  delete_status             tinyint(1) default 0,
  done_by                   varchar(255),
  done_at                   datetime,
  constraint pk_submission primary key (id))
;

create table supplied (
  id                        bigint auto_increment not null,
  item_id                   bigint,
  supplied_qty              integer,
  unit_price                double,
  fund                      varchar(255),
  supplier_id               bigint,
  begining_qty              integer,
  aquistion_date            datetime,
  expiration_date           datetime,
  date                      datetime,
  delete_status             tinyint(1) default 0,
  constraint pk_supplied primary key (id))
;

create table supplier (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  email                     varchar(255),
  address                   varchar(255),
  phone                     integer,
  tin_number                varchar(255),
  delete_status             tinyint(1) default 0,
  constraint pk_supplier primary key (id))
;

create table training (
  id                        bigint auto_increment not null,
  title                     varchar(255),
  trainer                   varchar(255),
  start_date                datetime,
  end_date                  datetime,
  start_date_application    datetime,
  end_date_application      datetime,
  duration                  integer,
  school_fees               double,
  accomodation_fees         double,
  restauration_fees         double,
  min_payment               double,
  other_fees                double,
  other_fees_spec           varchar(255),
  status                    tinyint(1) default 0,
  transcript                tinyint(1) default 0,
  transcript_print          tinyint(1) default 0,
  is_closed                 tinyint(1) default 0,
  is_closed_a               tinyint(1) default 0,
  i_mode_id                 bigint,
  graduation                datetime,
  delete_status             tinyint(1) default 0,
  has_graduated             tinyint(1) default 0,
  has_graduated_re_sit      tinyint(1) default 0,
  has_graduated_final       tinyint(1) default 0,
  is_marks_entered          tinyint(1) default 0,
  constraint pk_training primary key (id))
;

create table unit (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  acronym                   varchar(255),
  description               text,
  delete_status             tinyint(1) default 0,
  date                      datetime,
  constraint pk_unit primary key (id))
;

create table user_role (
  id                        bigint auto_increment not null,
  role_id                   bigint not null,
  user_id                   bigint not null,
  delete_status             tinyint(1) default 0,
  constraint pk_user_role primary key (id))
;

create table users (
  id                        bigint auto_increment not null,
  names                     varchar(255),
  profile                   varchar(255),
  email                     varchar(255),
  used_email                varchar(255),
  password                  varchar(255),
  phone                     varchar(255),
  code                      integer,
  role                      varchar(255),
  stating                   varchar(255),
  reset_code                varchar(255),
  path                      varchar(255),
  reset_required            tinyint(1) default 0,
  employee_id               bigint,
  delete_status             tinyint(1) default 0,
  constraint pk_users primary key (id))
;

create table verification (
  id                        bigint auto_increment not null,
  verifier_id               bigint,
  attachment_id             bigint,
  status                    tinyint(1) default 0,
  shelfed                   tinyint(1) default 0,
  user_comment              varchar(255),
  delete_status             tinyint(1) default 0,
  constraint pk_verification primary key (id))
;

alter table academic_files add constraint fk_academic_files_program_1 foreign key (program_id) references program (id) on delete restrict on update restrict;
create index ix_academic_files_program_1 on academic_files (program_id);
alter table academic_files add constraint fk_academic_files_session_2 foreign key (session_id) references session (id) on delete restrict on update restrict;
create index ix_academic_files_session_2 on academic_files (session_id);
alter table account add constraint fk_account_applicant_3 foreign key (applicant_id) references applicant (id) on delete restrict on update restrict;
create index ix_account_applicant_3 on account (applicant_id);
alter table anounce_role add constraint fk_anounce_role_role_4 foreign key (role_id) references roles (id) on delete restrict on update restrict;
create index ix_anounce_role_role_4 on anounce_role (role_id);
alter table anounce_role add constraint fk_anounce_role_announce_5 foreign key (announce_id) references announce (id) on delete restrict on update restrict;
create index ix_anounce_role_announce_5 on anounce_role (announce_id);
alter table applicant add constraint fk_applicant_user_6 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_applicant_user_6 on applicant (user_id);
alter table applicant add constraint fk_applicant_districts_7 foreign key (districts_id) references districts (id) on delete restrict on update restrict;
create index ix_applicant_districts_7 on applicant (districts_id);
alter table applied add constraint fk_applied_applicant_8 foreign key (applicant_id) references applicant (id) on delete restrict on update restrict;
create index ix_applied_applicant_8 on applied (applicant_id);
alter table applied add constraint fk_applied_training_9 foreign key (training_id) references training (id) on delete restrict on update restrict;
create index ix_applied_training_9 on applied (training_id);
alter table assignment add constraint fk_assignment_group_10 foreign key (group_id) references groups (id) on delete restrict on update restrict;
create index ix_assignment_group_10 on assignment (group_id);
alter table assignment add constraint fk_assignment_component_11 foreign key (component_id) references component (id) on delete restrict on update restrict;
create index ix_assignment_component_11 on assignment (component_id);
alter table assignment add constraint fk_assignment_lecture_12 foreign key (lecture_id) references lecture (id) on delete restrict on update restrict;
create index ix_assignment_lecture_12 on assignment (lecture_id);
alter table assignment add constraint fk_assignment_training_13 foreign key (training_id) references training (id) on delete restrict on update restrict;
create index ix_assignment_training_13 on assignment (training_id);
alter table assignment_result add constraint fk_assignment_result_assignment_14 foreign key (assignment_id) references assignment (id) on delete restrict on update restrict;
create index ix_assignment_result_assignment_14 on assignment_result (assignment_id);
alter table assignment_result add constraint fk_assignment_result_student_15 foreign key (student_id) references student (id) on delete restrict on update restrict;
create index ix_assignment_result_student_15 on assignment_result (student_id);
alter table attachment add constraint fk_attachment_app_16 foreign key (app_id) references applicant (id) on delete restrict on update restrict;
create index ix_attachment_app_16 on attachment (app_id);
alter table attachment add constraint fk_attachment_file_17 foreign key (file_id) references academic_files (id) on delete restrict on update restrict;
create index ix_attachment_file_17 on attachment (file_id);
alter table attendance add constraint fk_attendance_schedule_18 foreign key (schedule_id) references schedule (id) on delete restrict on update restrict;
create index ix_attendance_schedule_18 on attendance (schedule_id);
alter table attendance add constraint fk_attendance_component_19 foreign key (component_id) references component (id) on delete restrict on update restrict;
create index ix_attendance_component_19 on attendance (component_id);
alter table attendance add constraint fk_attendance_student_20 foreign key (student_id) references student (id) on delete restrict on update restrict;
create index ix_attendance_student_20 on attendance (student_id);
alter table campus_program add constraint fk_campus_program_campus_21 foreign key (campus_id) references campus (id) on delete restrict on update restrict;
create index ix_campus_program_campus_21 on campus_program (campus_id);
alter table campus_program add constraint fk_campus_program_program_22 foreign key (program_id) references program (id) on delete restrict on update restrict;
create index ix_campus_program_program_22 on campus_program (program_id);
alter table campus_program_mode add constraint fk_campus_program_mode_campusProgram_23 foreign key (campus_program_id) references campus_program (id) on delete restrict on update restrict;
create index ix_campus_program_mode_campusProgram_23 on campus_program_mode (campus_program_id);
alter table campus_program_mode add constraint fk_campus_program_mode_mode_24 foreign key (mode_id) references study_mode (id) on delete restrict on update restrict;
create index ix_campus_program_mode_mode_24 on campus_program_mode (mode_id);
alter table category add constraint fk_category_group_25 foreign key (group_id) references category_group (id) on delete restrict on update restrict;
create index ix_category_group_25 on category (group_id);
alter table chat_message add constraint fk_chat_message_sendTo_26 foreign key (send_to_id) references users (id) on delete restrict on update restrict;
create index ix_chat_message_sendTo_26 on chat_message (send_to_id);
alter table chat_message add constraint fk_chat_message_sendFrom_27 foreign key (send_from_id) references users (id) on delete restrict on update restrict;
create index ix_chat_message_sendFrom_27 on chat_message (send_from_id);
alter table checked add constraint fk_checked_user_28 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_checked_user_28 on checked (user_id);
alter table checked add constraint fk_checked_announce_29 foreign key (announce_id) references announce (id) on delete restrict on update restrict;
create index ix_checked_announce_29 on checked (announce_id);
alter table component add constraint fk_component_module_30 foreign key (module_id) references module (id) on delete restrict on update restrict;
create index ix_component_module_30 on component (module_id);
alter table component_max add constraint fk_component_max_component_31 foreign key (component_id) references component (id) on delete restrict on update restrict;
create index ix_component_max_component_31 on component_max (component_id);
alter table component_max add constraint fk_component_max_lecture_32 foreign key (lecture_id) references lecture (id) on delete restrict on update restrict;
create index ix_component_max_lecture_32 on component_max (lecture_id);
alter table component_max add constraint fk_component_max_training_33 foreign key (training_id) references training (id) on delete restrict on update restrict;
create index ix_component_max_training_33 on component_max (training_id);
alter table course_material add constraint fk_course_material_schedule_34 foreign key (schedule_id) references schedule (id) on delete restrict on update restrict;
create index ix_course_material_schedule_34 on course_material (schedule_id);
alter table damage add constraint fk_damage_student_35 foreign key (student_id) references student (id) on delete restrict on update restrict;
create index ix_damage_student_35 on damage (student_id);
alter table day_session add constraint fk_day_session_day_36 foreign key (day_id) references days (id) on delete restrict on update restrict;
create index ix_day_session_day_36 on day_session (day_id);
alter table day_session add constraint fk_day_session_session_37 foreign key (session_id) references session (id) on delete restrict on update restrict;
create index ix_day_session_session_37 on day_session (session_id);
alter table districts add constraint fk_districts_provinces_38 foreign key (provinces_id) references provinces (id) on delete restrict on update restrict;
create index ix_districts_provinces_38 on districts (provinces_id);
alter table employee add constraint fk_employee_unit_39 foreign key (unit_id) references unit (id) on delete restrict on update restrict;
create index ix_employee_unit_39 on employee (unit_id);
alter table employee add constraint fk_employee_position_40 foreign key (position_id) references position (id) on delete restrict on update restrict;
create index ix_employee_position_40 on employee (position_id);
alter table ev_question add constraint fk_ev_question_category_41 foreign key (category_id) references ev_category (id) on delete restrict on update restrict;
create index ix_ev_question_category_41 on ev_question (category_id);
alter table evaluation add constraint fk_evaluation_lecture_42 foreign key (lecture_id) references lecture (id) on delete restrict on update restrict;
create index ix_evaluation_lecture_42 on evaluation (lecture_id);
alter table evaluation add constraint fk_evaluation_student_43 foreign key (student_id) references student (id) on delete restrict on update restrict;
create index ix_evaluation_student_43 on evaluation (student_id);
alter table evaluation add constraint fk_evaluation_schedule_44 foreign key (schedule_id) references schedule (id) on delete restrict on update restrict;
create index ix_evaluation_schedule_44 on evaluation (schedule_id);
alter table evaluation add constraint fk_evaluation_question_45 foreign key (question_id) references ev_question (id) on delete restrict on update restrict;
create index ix_evaluation_question_45 on evaluation (question_id);
alter table group_members add constraint fk_group_members_student_46 foreign key (student_id) references student (id) on delete restrict on update restrict;
create index ix_group_members_student_46 on group_members (student_id);
alter table group_members add constraint fk_group_members_groups_47 foreign key (groups_id) references groups (id) on delete restrict on update restrict;
create index ix_group_members_groups_47 on group_members (groups_id);
alter table group_submission add constraint fk_group_submission_groups_48 foreign key (groups_id) references groups (id) on delete restrict on update restrict;
create index ix_group_submission_groups_48 on group_submission (groups_id);
alter table group_submission add constraint fk_group_submission_assignment_49 foreign key (assignment_id) references assignment (id) on delete restrict on update restrict;
create index ix_group_submission_assignment_49 on group_submission (assignment_id);
alter table group_submission add constraint fk_group_submission_student_50 foreign key (student_id) references student (id) on delete restrict on update restrict;
create index ix_group_submission_student_50 on group_submission (student_id);
alter table groups add constraint fk_groups_component_51 foreign key (component_id) references component (id) on delete restrict on update restrict;
create index ix_groups_component_51 on groups (component_id);
alter table groups add constraint fk_groups_training_52 foreign key (training_id) references training (id) on delete restrict on update restrict;
create index ix_groups_training_52 on groups (training_id);
alter table hour_session add constraint fk_hour_session_hour_53 foreign key (hour_id) references hours (id) on delete restrict on update restrict;
create index ix_hour_session_hour_53 on hour_session (hour_id);
alter table hour_session add constraint fk_hour_session_session_54 foreign key (session_id) references session (id) on delete restrict on update restrict;
create index ix_hour_session_session_54 on hour_session (session_id);
alter table ildplibrary add constraint fk_ildplibrary_student_55 foreign key (student_id) references student (id) on delete restrict on update restrict;
create index ix_ildplibrary_student_55 on ildplibrary (student_id);
alter table intake add constraint fk_intake_year_56 foreign key (year_id) references academic_year (id) on delete restrict on update restrict;
create index ix_intake_year_56 on intake (year_id);
alter table intake_session_mode add constraint fk_intake_session_mode_intake_57 foreign key (intake_id) references intake (id) on delete restrict on update restrict;
create index ix_intake_session_mode_intake_57 on intake_session_mode (intake_id);
alter table intake_session_mode add constraint fk_intake_session_mode_sessionMode_58 foreign key (session_mode_id) references session_mode (id) on delete restrict on update restrict;
create index ix_intake_session_mode_sessionMode_58 on intake_session_mode (session_mode_id);
alter table intake_session_mode add constraint fk_intake_session_mode_campusProgram_59 foreign key (campus_program_id) references campus_program (id) on delete restrict on update restrict;
create index ix_intake_session_mode_campusProgram_59 on intake_session_mode (campus_program_id);
alter table internship add constraint fk_internship_student_60 foreign key (student_id) references student (id) on delete restrict on update restrict;
create index ix_internship_student_60 on internship (student_id);
alter table internship_appeal add constraint fk_internship_appeal_applicant_61 foreign key (applicant_id) references applicant (id) on delete restrict on update restrict;
create index ix_internship_appeal_applicant_61 on internship_appeal (applicant_id);
alter table item add constraint fk_item_category_62 foreign key (category_id) references category (id) on delete restrict on update restrict;
create index ix_item_category_62 on item (category_id);
alter table lecture add constraint fk_lecture_user_63 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_lecture_user_63 on lecture (user_id);
alter table location add constraint fk_location_block_64 foreign key (block_id) references block (id) on delete restrict on update restrict;
create index ix_location_block_64 on location (block_id);
alter table mark_sheet add constraint fk_mark_sheet_student_65 foreign key (student_id) references student (id) on delete restrict on update restrict;
create index ix_mark_sheet_student_65 on mark_sheet (student_id);
alter table mark_sheet add constraint fk_mark_sheet_module_66 foreign key (module_id) references module (id) on delete restrict on update restrict;
create index ix_mark_sheet_module_66 on mark_sheet (module_id);
alter table module add constraint fk_module_lecture_67 foreign key (lecture_id) references lecture (id) on delete restrict on update restrict;
create index ix_module_lecture_67 on module (lecture_id);
alter table module add constraint fk_module_program_68 foreign key (program_id) references program (id) on delete restrict on update restrict;
create index ix_module_program_68 on module (program_id);
alter table payment add constraint fk_payment_account_69 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_payment_account_69 on payment (account_id);
alter table payment add constraint fk_payment_bankAccount_70 foreign key (bank_account_id) references bank_account (id) on delete restrict on update restrict;
create index ix_payment_bankAccount_70 on payment (bank_account_id);
alter table re_sit_re_take_request add constraint fk_re_sit_re_take_request_student_71 foreign key (student_id) references student (id) on delete restrict on update restrict;
create index ix_re_sit_re_take_request_student_71 on re_sit_re_take_request (student_id);
alter table re_sit_re_take_request add constraint fk_re_sit_re_take_request_training_72 foreign key (training_id) references training (id) on delete restrict on update restrict;
create index ix_re_sit_re_take_request_training_72 on re_sit_re_take_request (training_id);
alter table refund add constraint fk_refund_account_73 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_refund_account_73 on refund (account_id);
alter table refund add constraint fk_refund_bankAccount_74 foreign key (bank_account_id) references bank_account (id) on delete restrict on update restrict;
create index ix_refund_bankAccount_74 on refund (bank_account_id);
alter table refund add constraint fk_refund_refundRequest_75 foreign key (refund_request_id) references refund_request (id) on delete restrict on update restrict;
create index ix_refund_refundRequest_75 on refund (refund_request_id);
alter table refund_request add constraint fk_refund_request_account_76 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_refund_request_account_76 on refund_request (account_id);
alter table request add constraint fk_request_employee_77 foreign key (employee_id) references employee (id) on delete restrict on update restrict;
create index ix_request_employee_77 on request (employee_id);
alter table request_details add constraint fk_request_details_request_78 foreign key (request_id) references re_sit_re_take_request (id) on delete restrict on update restrict;
create index ix_request_details_request_78 on request_details (request_id);
alter table request_details add constraint fk_request_details_module_79 foreign key (module_id) references module (id) on delete restrict on update restrict;
create index ix_request_details_module_79 on request_details (module_id);
alter table room add constraint fk_room_campus_80 foreign key (campus_id) references campus (id) on delete restrict on update restrict;
create index ix_room_campus_80 on room (campus_id);
alter table schedule add constraint fk_schedule_lecture_81 foreign key (lecture_id) references lecture (id) on delete restrict on update restrict;
create index ix_schedule_lecture_81 on schedule (lecture_id);
alter table schedule add constraint fk_schedule_component_82 foreign key (component_id) references component (id) on delete restrict on update restrict;
create index ix_schedule_component_82 on schedule (component_id);
alter table schedule add constraint fk_schedule_room_83 foreign key (room_id) references room (id) on delete restrict on update restrict;
create index ix_schedule_room_83 on schedule (room_id);
alter table schedule add constraint fk_schedule_training_84 foreign key (training_id) references training (id) on delete restrict on update restrict;
create index ix_schedule_training_84 on schedule (training_id);
alter table schedule add constraint fk_schedule_dateRange_85 foreign key (date_range_id) references date_range (id) on delete restrict on update restrict;
create index ix_schedule_dateRange_85 on schedule (date_range_id);
alter table school_fees add constraint fk_school_fees_intake_86 foreign key (intake_id) references intake (id) on delete restrict on update restrict;
create index ix_school_fees_intake_86 on school_fees (intake_id);
alter table school_fees add constraint fk_school_fees_programMode_87 foreign key (program_mode_id) references campus_program_mode (id) on delete restrict on update restrict;
create index ix_school_fees_programMode_87 on school_fees (program_mode_id);
alter table school_fees add constraint fk_school_fees_sessionMode_88 foreign key (session_mode_id) references session_mode (id) on delete restrict on update restrict;
create index ix_school_fees_sessionMode_88 on school_fees (session_mode_id);
alter table sector add constraint fk_sector_districts_89 foreign key (districts_id) references districts (id) on delete restrict on update restrict;
create index ix_sector_districts_89 on sector (districts_id);
alter table session_mode add constraint fk_session_mode_session_90 foreign key (session_id) references session (id) on delete restrict on update restrict;
create index ix_session_mode_session_90 on session_mode (session_id);
alter table session_mode add constraint fk_session_mode_mode_91 foreign key (mode_id) references study_mode (id) on delete restrict on update restrict;
create index ix_session_mode_mode_91 on session_mode (mode_id);
alter table stock_movement add constraint fk_stock_movement_item_92 foreign key (item_id) references item (id) on delete restrict on update restrict;
create index ix_stock_movement_item_92 on stock_movement (item_id);
alter table stock_movement add constraint fk_stock_movement_request_93 foreign key (request_id) references request (id) on delete restrict on update restrict;
create index ix_stock_movement_request_93 on stock_movement (request_id);
alter table stock_movement add constraint fk_stock_movement_location_94 foreign key (location_id) references location (id) on delete restrict on update restrict;
create index ix_stock_movement_location_94 on stock_movement (location_id);
alter table student add constraint fk_student_applicant_95 foreign key (applicant_id) references applicant (id) on delete restrict on update restrict;
create index ix_student_applicant_95 on student (applicant_id);
alter table student add constraint fk_student_training_96 foreign key (training_id) references training (id) on delete restrict on update restrict;
create index ix_student_training_96 on student (training_id);
alter table sub_mark add constraint fk_sub_mark_student_97 foreign key (student_id) references student (id) on delete restrict on update restrict;
create index ix_sub_mark_student_97 on sub_mark (student_id);
alter table sub_mark add constraint fk_sub_mark_component_98 foreign key (component_id) references component (id) on delete restrict on update restrict;
create index ix_sub_mark_component_98 on sub_mark (component_id);
alter table submission add constraint fk_submission_assignment_99 foreign key (assignment_id) references assignment (id) on delete restrict on update restrict;
create index ix_submission_assignment_99 on submission (assignment_id);
alter table submission add constraint fk_submission_student_100 foreign key (student_id) references student (id) on delete restrict on update restrict;
create index ix_submission_student_100 on submission (student_id);
alter table supplied add constraint fk_supplied_item_101 foreign key (item_id) references item (id) on delete restrict on update restrict;
create index ix_supplied_item_101 on supplied (item_id);
alter table supplied add constraint fk_supplied_supplier_102 foreign key (supplier_id) references supplier (id) on delete restrict on update restrict;
create index ix_supplied_supplier_102 on supplied (supplier_id);
alter table training add constraint fk_training_iMode_103 foreign key (i_mode_id) references intake_session_mode (id) on delete restrict on update restrict;
create index ix_training_iMode_103 on training (i_mode_id);
alter table user_role add constraint fk_user_role_role_104 foreign key (role_id) references roles (id) on delete restrict on update restrict;
create index ix_user_role_role_104 on user_role (role_id);
alter table user_role add constraint fk_user_role_user_105 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_user_role_user_105 on user_role (user_id);
alter table users add constraint fk_users_employee_106 foreign key (employee_id) references employee (id) on delete restrict on update restrict;
create index ix_users_employee_106 on users (employee_id);
alter table verification add constraint fk_verification_verifier_107 foreign key (verifier_id) references users (id) on delete restrict on update restrict;
create index ix_verification_verifier_107 on verification (verifier_id);
alter table verification add constraint fk_verification_attachment_108 foreign key (attachment_id) references attachment (id) on delete restrict on update restrict;
create index ix_verification_attachment_108 on verification (attachment_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table academic_files;

drop table academic_year;

drop table account;

drop table announce;

drop table anounce_role;

drop table applicant;

drop table applied;

drop table assignment;

drop table assignment_result;

drop table attachment;

drop table attendance;

drop table bank_account;

drop table block;

drop table campus;

drop table campus_program;

drop table campus_program_mode;

drop table category;

drop table chat_message;

drop table checked;

drop table component;

drop table component_max;

drop table country;

drop table course_material;

drop table damage;

drop table date_range;

drop table day_session;

drop table days;

drop table districts;

drop table employee;

drop table english_skills;

drop table ev_category;

drop table ev_question;

drop table evaluation;

drop table event;

drop table category_group;

drop table group_members;

drop table group_submission;

drop table groups;

drop table hour_session;

drop table hours;

drop table ildplibrary;

drop table intake;

drop table intake_session_mode;

drop table internship;

drop table internship_appeal;

drop table item;

drop table lecture;

drop table location;

drop table mark_sheet;

drop table module;

drop table payment;

drop table position;

drop table profile_info;

drop table program;

drop table provinces;

drop table re_sit_re_take_request;

drop table refund;

drop table refund_request;

drop table request;

drop table request_details;

drop table roles;

drop table room;

drop table schedule;

drop table school_fees;

drop table sector;

drop table session;

drop table session_mode;

drop table source;

drop table stock_movement;

drop table student;

drop table study_mode;

drop table sub_mark;

drop table submission;

drop table supplied;

drop table supplier;

drop table training;

drop table unit;

drop table user_role;

drop table users;

drop table verification;

SET FOREIGN_KEY_CHECKS=1;

