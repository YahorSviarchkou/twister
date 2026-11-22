INSERT INTO public.countries (id, created_at, created_by, updated_at, updated_by, description, "name")
VALUES(nextval('countries_id_seq'::regclass), '', '', '', '', '', '');

INSERT INTO public.customer_transport (customer_id, transport_id)
VALUES(0, 0);

INSERT INTO public.customers (id, created_at, created_by, updated_at, updated_by, "name", patronymic, phone, surname)
VALUES(nextval('customers_id_seq'::regclass), '', '', '', '', '', '', '', '');

INSERT INTO public.repair_invoices (id, created_at, created_by, updated_at, updated_by, description, diagnostic_price, labor_price, spare_price, order_id)
VALUES(nextval('repair_invoices_id_seq'::regclass), '', '', '', '', '', 0, 0, 0, 0);

INSERT INTO public.repair_order_status_history (id, created_at, created_by, updated_at, updated_by, status, workflow_item_id)
VALUES(nextval('repair_order_status_history_id_seq'::regclass), '', '', '', '', '', 0);

INSERT INTO public.repair_orders (id, created_at, created_by, updated_at, updated_by, description, customer_id, transport_id)
VALUES(nextval('repair_orders_id_seq'::regclass), '', '', '', '', '', 0, 0);

INSERT INTO public.repair_task_item_status_history (id, created_at, created_by, updated_at, updated_by, status, workflow_item_id)
VALUES(nextval('repair_task_item_status_history_id_seq'::regclass), '', '', '', '', '', 0);

INSERT INTO public.repair_task_items (id, created_at, created_by, updated_at, updated_by, service_type_id, task_id)
VALUES(nextval('repair_task_items_id_seq'::regclass), '', '', '', '', 0, 0);

INSERT INTO public.repair_task_status_history (id, created_at, created_by, updated_at, updated_by, status, workflow_item_id)
VALUES(nextval('repair_task_status_history_id_seq'::regclass), '', '', '', '', '', 0);

INSERT INTO public.repair_tasks (id, created_at, created_by, updated_at, updated_by, order_id)
VALUES(nextval('repair_tasks_id_seq'::regclass), '', '', '', '', 0);

INSERT INTO public.service_types (id, created_at, created_by, updated_at, updated_by, description, "name", price)
VALUES(nextval('service_types_id_seq'::regclass), '', '', '', '', '', '', 0);

INSERT INTO public.spare_brands (id, created_at, created_by, updated_at, updated_by, description, "name")
VALUES(nextval('spare_brands_id_seq'::regclass), '', '', '', '', '', '');

INSERT INTO public.spare_types (id, created_at, created_by, updated_at, updated_by, description, "name")
VALUES(nextval('spare_types_id_seq'::regclass), '', '', '', '', '', '');

INSERT INTO public.spares (id, created_at, created_by, updated_at, updated_by, description, "name", issue_year, model, sku, url, material, price, warehouse_quantity, brand_id, country_id, type_id, transport_type_id)
VALUES(nextval('spares_id_seq'::regclass), '', '', '', '', '', '', 0, '', '', '', '', 0, 0, 0, 0, 0, 0);

INSERT INTO public.task_item_spares (item_id, spare_id)
VALUES(0, 0);

INSERT INTO public.transport (id, created_at, created_by, updated_at, updated_by, description, "name", issue_year, model, sku, url, brand_id, country_id, type_id)
VALUES(nextval('transport_id_seq'::regclass), '', '', '', '', '', '', 0, '', '', '', 0, 0, 0);

INSERT INTO public.transport_brands (id, created_at, created_by, updated_at, updated_by, description, "name")
VALUES(nextval('transport_brands_id_seq'::regclass), '', '', '', '', '', '');

INSERT INTO public.transport_types (id, created_at, created_by, updated_at, updated_by, description, "name")
VALUES(nextval('transport_types_id_seq'::regclass), '', '', '', '', '', '');

INSERT INTO public.users (id, created_at, created_by, updated_at, updated_by, email, login, "name", "password", phone, "role")
VALUES(?, '', '', '', '', '', '', '', '', '', '');
