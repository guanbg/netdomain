alter table fms_library add constraint fk_reference_44 foreign key (parent_id) references fms_library (fs_id) on delete cascade on update restrict;
