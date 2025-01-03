ALTER TABLE `profile_info` ADD `stamp` VARCHAR(255) NOT NULL DEFAULT ' ' AFTER `rector`;

ALTER TABLE `assignment_result` CHANGE `result` `result` FLOAT NULL DEFAULT '0.0';

ALTER TABLE `assignment_result` CHANGE `result` `result` DOUBLE NULL DEFAULT '0';

-- NEW QUERY 02012025
--this new column aim to remove speculation happen about the tuition fee that was payed but not accordingly
ALTER TABLE training add eac_student_tuition_fees DOUBLE NOT NULL DEFAULT '0.0';
ALTER TABLE training add non_eac_student_tuition_fees DOUBLE NOT NULL DEFAULT '0.0';
ALTER TABLE training add min_eac_student_tuition_fees DOUBLE NOT NULL DEFAULT '0.0';
ALTER TABLE training add min_non_eac_student_tuition_fees DOUBLE NOT NULL DEFAULT '0.0';
