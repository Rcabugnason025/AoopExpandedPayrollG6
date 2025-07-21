-- =====================================================
-- MotorPH Payroll System Database - 3NF Normalized
-- ENHANCED with AOOP Integration Support
-- =====================================================

-- Drop existing database if exists and create new one
DROP DATABASE IF EXISTS aoopdatabase_payroll;
CREATE DATABASE aoopdatabase_payroll;
USE aoopdatabase_payroll;

-- =====================================================
-- 1. NORMALIZED TABLES (3NF COMPLIANT) - ENHANCED
-- =====================================================

-- Table 1: Departments (Reference table)
CREATE TABLE departments (
    department_id INT PRIMARY KEY AUTO_INCREMENT,
    department_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    manager_id INT NULL,
    budget DECIMAL(15,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_dept_name (department_name),
    INDEX idx_dept_active (is_active)
);

-- Table 2: Positions (Reference table)  
CREATE TABLE positions (
    position_id INT PRIMARY KEY AUTO_INCREMENT,
    position_title VARCHAR(100) NOT NULL UNIQUE,
    department_id INT,
    min_salary DECIMAL(10,2) DEFAULT 0.00,
    max_salary DECIMAL(10,2) DEFAULT 0.00,
    job_description TEXT,
    requirements TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(department_id),
    INDEX idx_position_dept (department_id),
    INDEX idx_position_active (is_active)
);

-- Table 3: Employee Status Types
CREATE TABLE employee_status_types (
    status_id INT PRIMARY KEY AUTO_INCREMENT,
    status_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    benefits_eligible BOOLEAN DEFAULT FALSE,
    leave_eligible BOOLEAN DEFAULT FALSE,
    overtime_eligible BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 4: Employees (Main employee information) - ENHANCED
CREATE TABLE employees (
    employee_id INT PRIMARY KEY,
    last_name VARCHAR(100) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    birthday DATE,
    address TEXT,
    phone_number VARCHAR(20),
    email VARCHAR(100),
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    sss_number VARCHAR(20),
    philhealth_number VARCHAR(20),
    tin_number VARCHAR(20),
    pagibig_number VARCHAR(20),
    status_id INT,
    position_id INT,
    department_id INT,
    immediate_supervisor_id INT,
    date_hired DATE DEFAULT (CURRENT_DATE),
    date_terminated DATE NULL,
    termination_reason TEXT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (status_id) REFERENCES employee_status_types(status_id),
    FOREIGN KEY (position_id) REFERENCES positions(position_id),
    FOREIGN KEY (department_id) REFERENCES departments(department_id),
    FOREIGN KEY (immediate_supervisor_id) REFERENCES employees(employee_id),
    INDEX idx_emp_name (last_name, first_name),
    INDEX idx_emp_status (status_id),
    INDEX idx_emp_position (position_id),
    INDEX idx_emp_dept (department_id),
    INDEX idx_emp_supervisor (immediate_supervisor_id),
    INDEX idx_emp_active (is_active),
    INDEX idx_emp_hired_date (date_hired)
);

-- Table 5: Employee Salary Information (Separated for normalization) - ENHANCED
CREATE TABLE employee_salary (
    salary_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    basic_salary DECIMAL(10,2) NOT NULL,
    rice_subsidy DECIMAL(8,2) DEFAULT 1500.00,
    phone_allowance DECIMAL(8,2) DEFAULT 0.00,
    clothing_allowance DECIMAL(8,2) DEFAULT 0.00,
    gross_semimonthly_rate DECIMAL(10,2) NOT NULL,
    hourly_rate DECIMAL(8,2) NOT NULL,
    daily_rate DECIMAL(8,2) GENERATED ALWAYS AS (basic_salary / 22) STORED,
    effective_date DATE DEFAULT (CURRENT_DATE),
    end_date DATE NULL,
    is_active BOOLEAN DEFAULT TRUE,
    salary_grade VARCHAR(20),
    approved_by INT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES employees(employee_id),
    INDEX idx_employee_active (employee_id, is_active),
    INDEX idx_effective_date (effective_date),
    INDEX idx_salary_range (basic_salary)
);

-- Table 6: Attendance Records - ENHANCED
CREATE TABLE attendance (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    log_in TIME,
    log_out TIME,
    break_start TIME,
    break_end TIME,
    hours_worked DECIMAL(4,2) DEFAULT 0.00,
    overtime_hours DECIMAL(4,2) DEFAULT 0.00,
    undertime_hours DECIMAL(4,2) DEFAULT 0.00,
    late_minutes INT DEFAULT 0,
    early_out_minutes INT DEFAULT 0,
    attendance_status ENUM('Present', 'Absent', 'Late', 'Half_Day', 'On_Leave') DEFAULT 'Present',
    remarks TEXT,
    approved_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES employees(employee_id),
    UNIQUE KEY unique_employee_date (employee_id, attendance_date),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_employee_date (employee_id, attendance_date),
    INDEX idx_attendance_status (attendance_status)
);

-- Table 7: Payroll Periods - ENHANCED
CREATE TABLE payroll_periods (
    period_id INT PRIMARY KEY AUTO_INCREMENT,
    period_name VARCHAR(100) NOT NULL,
    period_start_date DATE NOT NULL,
    period_end_date DATE NOT NULL,
    cut_off_date DATE NOT NULL,
    pay_date DATE NOT NULL,
    payroll_type ENUM('semimonthly', 'monthly') DEFAULT 'semimonthly',
    status ENUM('open', 'calculated', 'approved', 'paid', 'closed') DEFAULT 'open',
    is_processed BOOLEAN DEFAULT FALSE,
    processed_date TIMESTAMP NULL,
    processed_by INT,
    total_gross_pay DECIMAL(15,2) DEFAULT 0.00,
    total_deductions DECIMAL(15,2) DEFAULT 0.00,
    total_net_pay DECIMAL(15,2) DEFAULT 0.00,
    employee_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (processed_by) REFERENCES employees(employee_id),
    UNIQUE KEY unique_period (period_start_date, period_end_date),
    INDEX idx_period_dates (period_start_date, period_end_date),
    INDEX idx_period_status (status)
);

-- Table 8: Payroll Records - ENHANCED
CREATE TABLE payroll (
    payroll_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    period_id INT NOT NULL,
    days_worked INT DEFAULT 0,
    hours_worked DECIMAL(6,2) DEFAULT 0.00,
    regular_hours DECIMAL(6,2) DEFAULT 0.00,
    overtime_hours DECIMAL(6,2) DEFAULT 0.00,
    holiday_hours DECIMAL(6,2) DEFAULT 0.00,
    basic_pay DECIMAL(10,2) NOT NULL,
    overtime_pay DECIMAL(10,2) DEFAULT 0.00,
    holiday_pay DECIMAL(10,2) DEFAULT 0.00,
    night_differential DECIMAL(10,2) DEFAULT 0.00,
    rice_subsidy DECIMAL(8,2) DEFAULT 0.00,
    phone_allowance DECIMAL(8,2) DEFAULT 0.00,
    clothing_allowance DECIMAL(8,2) DEFAULT 0.00,
    other_allowances DECIMAL(10,2) DEFAULT 0.00,
    gross_pay DECIMAL(10,2) NOT NULL,
    sss_deduction DECIMAL(8,2) DEFAULT 0.00,
    philhealth_deduction DECIMAL(8,2) DEFAULT 0.00,
    pagibig_deduction DECIMAL(8,2) DEFAULT 0.00,
    tax_deduction DECIMAL(8,2) DEFAULT 0.00,
    late_deduction DECIMAL(8,2) DEFAULT 0.00,
    undertime_deduction DECIMAL(8,2) DEFAULT 0.00,
    unpaid_leave_deduction DECIMAL(8,2) DEFAULT 0.00,
    other_deductions DECIMAL(10,2) DEFAULT 0.00,
    total_deductions DECIMAL(10,2) DEFAULT 0.00,
    net_pay DECIMAL(10,2) NOT NULL,
    status ENUM('draft', 'calculated', 'approved', 'paid') DEFAULT 'draft',
    approved_by INT,
    approved_date TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    FOREIGN KEY (period_id) REFERENCES payroll_periods(period_id),
    FOREIGN KEY (approved_by) REFERENCES employees(employee_id),
    UNIQUE KEY unique_employee_period (employee_id, period_id),
    INDEX idx_period_payroll (period_id),
    INDEX idx_employee_payroll (employee_id),
    INDEX idx_payroll_status (status)
);

-- Table 9: Leave Types - NEW
CREATE TABLE leave_types (
    leave_type_id INT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    max_days_per_year INT DEFAULT 0,
    is_paid BOOLEAN DEFAULT TRUE,
    requires_approval BOOLEAN DEFAULT TRUE,
    advance_notice_days INT DEFAULT 1,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 10: Leave Requests - ENHANCED
CREATE TABLE leave_requests (
    leave_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    leave_type_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    days_requested DECIMAL(3,1) NOT NULL,
    reason TEXT,
    status ENUM('pending', 'approved', 'rejected', 'cancelled') DEFAULT 'pending',
    approved_by INT,
    approved_date TIMESTAMP NULL,
    rejection_reason TEXT,
    supporting_documents TEXT,
    emergency_contact_notified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    FOREIGN KEY (leave_type_id) REFERENCES leave_types(leave_type_id),
    FOREIGN KEY (approved_by) REFERENCES employees(employee_id),
    INDEX idx_employee_leave (employee_id),
    INDEX idx_leave_dates (start_date, end_date),
    INDEX idx_leave_status (status),
    INDEX idx_leave_type (leave_type_id)
);

-- Table 11: Overtime Requests - ENHANCED  
CREATE TABLE overtime_requests (
    overtime_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    request_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    hours_requested DECIMAL(4,2) NOT NULL,
    reason TEXT NOT NULL,
    justification TEXT,
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    approved_by INT,
    approved_date TIMESTAMP NULL,
    actual_hours DECIMAL(4,2),
    rejection_reason TEXT,
    priority ENUM('low', 'medium', 'high', 'critical') DEFAULT 'medium',
    project_code VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES employees(employee_id),
    INDEX idx_employee_overtime (employee_id),
    INDEX idx_overtime_date (request_date),
    INDEX idx_overtime_status (status)
);

-- Table 12: Deduction Types - NEW
CREATE TABLE deduction_types (
    deduction_type_id INT PRIMARY KEY AUTO_INCREMENT,
    type_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_mandatory BOOLEAN DEFAULT FALSE,
    is_percentage BOOLEAN DEFAULT FALSE,
    default_amount DECIMAL(10,2) DEFAULT 0.00,
    max_amount DECIMAL(10,2),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table 13: Employee Deductions - ENHANCED
CREATE TABLE employee_deductions (
    deduction_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    deduction_type_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    description TEXT,
    deduction_date DATE DEFAULT (CURRENT_DATE),
    effective_date DATE DEFAULT (CURRENT_DATE),
    end_date DATE,
    is_recurring BOOLEAN DEFAULT FALSE,
    frequency ENUM('once', 'weekly', 'monthly', 'quarterly', 'annually') DEFAULT 'once',
    remaining_installments INT DEFAULT 1,
    status ENUM('active', 'completed', 'suspended') DEFAULT 'active',
    approved_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    FOREIGN KEY (deduction_type_id) REFERENCES deduction_types(deduction_type_id),
    FOREIGN KEY (approved_by) REFERENCES employees(employee_id),
    INDEX idx_employee_deductions (employee_id),
    INDEX idx_deduction_type (deduction_type_id),
    INDEX idx_deduction_status (status)
);

-- Table 14: System Users & Authentication - NEW
CREATE TABLE system_users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('employee', 'hr', 'payroll', 'manager', 'admin', 'executive') NOT NULL DEFAULT 'employee',
    permissions JSON,
    last_login TIMESTAMP NULL,
    failed_login_attempts INT DEFAULT 0,
    account_locked_until TIMESTAMP NULL,
    password_changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    must_change_password BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    INDEX idx_username (username),
    INDEX idx_employee_user (employee_id),
    INDEX idx_user_role (role),
    INDEX idx_user_active (is_active)
);

-- Table 15: Audit Log - NEW
CREATE TABLE audit_log (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    employee_id INT,
    action VARCHAR(100) NOT NULL,
    table_name VARCHAR(100),
    record_id INT,
    old_values JSON,
    new_values JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES system_users(user_id),
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_employee (employee_id),
    INDEX idx_audit_action (action),
    INDEX idx_audit_table (table_name),
    INDEX idx_audit_timestamp (timestamp)
);

-- =====================================================
-- 2. INSERT REFERENCE DATA - ENHANCED
-- =====================================================

-- Insert Departments
INSERT INTO departments (department_name, description, budget) VALUES
('Executive', 'C-Level executives and senior leadership', 5000000.00),
('Operations', 'Core business operations and production', 3000000.00),
('Finance', 'Financial management and accounting', 2000000.00),
('Marketing', 'Marketing and brand management', 1500000.00),
('Information Technology', 'IT infrastructure and development', 2500000.00),
('Human Resources', 'Employee management and development', 1000000.00),
('Sales', 'Sales and customer acquisition', 2000000.00),
('Supply Chain', 'Procurement and logistics', 1500000.00),
('Customer Service', 'Customer support and relations', 800000.00),
('Legal', 'Legal affairs and compliance', 600000.00);

-- Insert Employee Status Types
INSERT INTO employee_status_types (status_name, description, benefits_eligible, leave_eligible, overtime_eligible) VALUES
('Regular', 'Permanent full-time employee', TRUE, TRUE, TRUE),
('Probationary', 'Employee under probationary period', TRUE, FALSE, TRUE),
('Contractual', 'Fixed-term contract employee', FALSE, FALSE, FALSE),
('Part-time', 'Part-time employee', FALSE, FALSE, FALSE),
('Consultant', 'External consultant', FALSE, FALSE, FALSE),
('Intern', 'Student intern or trainee', FALSE, FALSE, FALSE),
('Resigned', 'Former employee who resigned', FALSE, FALSE, FALSE),
('Terminated', 'Former employee who was terminated', FALSE, FALSE, FALSE);

-- Insert Positions
INSERT INTO positions (position_title, department_id, min_salary, max_salary, job_description) VALUES
('Chief Executive Officer', 1, 200000, 500000, 'Chief Executive Officer responsible for overall company strategy'),
('Chief Operating Officer', 2, 150000, 300000, 'Chief Operating Officer overseeing daily operations'),
('Chief Finance Officer', 3, 120000, 250000, 'Chief Financial Officer managing financial strategy'),
('Chief Marketing Officer', 4, 100000, 200000, 'Chief Marketing Officer leading marketing initiatives'),
('IT Director', 5, 80000, 150000, 'IT Director managing technology infrastructure'),
('HR Manager', 6, 60000, 120000, 'HR Manager overseeing human resources'),
('HR Team Leader', 6, 45000, 80000, 'HR Team Leader managing HR operations'),
('HR Specialist', 6, 35000, 60000, 'HR Specialist handling employee relations'),
('Accounting Manager', 3, 55000, 100000, 'Accounting Manager overseeing financial records'),
('Payroll Manager', 3, 50000, 90000, 'Payroll Manager managing payroll operations'),
('Payroll Specialist', 3, 35000, 65000, 'Payroll Specialist processing payroll'),
('Sales Manager', 7, 60000, 120000, 'Sales Manager leading sales team'),
('Sales Representative', 7, 30000, 60000, 'Sales Representative managing client accounts'),
('Customer Service Manager', 9, 45000, 80000, 'Customer Service Manager overseeing support'),
('Customer Service Representative', 9, 25000, 45000, 'Customer Service Representative assisting customers'),
('Supply Chain Manager', 8, 55000, 100000, 'Supply Chain Manager managing logistics'),
('Operations Supervisor', 2, 40000, 70000, 'Operations Supervisor overseeing production'),
('IT System Administrator', 5, 45000, 80000, 'IT Administrator managing systems');

-- Insert Leave Types
INSERT INTO leave_types (type_name, description, max_days_per_year, is_paid, requires_approval, advance_notice_days) VALUES
('Annual Leave', 'Vacation leave for rest and recreation', 15, TRUE, TRUE, 3),
('Sick Leave', 'Medical leave for illness or medical appointments', 10, TRUE, TRUE, 0),
('Emergency Leave', 'Urgent leave for family emergencies', 5, TRUE, TRUE, 0),
('Maternity Leave', 'Maternity leave for new mothers', 105, TRUE, TRUE, 30),
('Paternity Leave', 'Paternity leave for new fathers', 7, TRUE, TRUE, 7),
('Bereavement Leave', 'Leave for death of immediate family member', 3, TRUE, TRUE, 0),
('Personal Leave', 'Personal time off for personal matters', 3, FALSE, TRUE, 1),
('Study Leave', 'Leave for educational purposes', 10, FALSE, TRUE, 14),
('Service Incentive Leave', 'Government mandated service incentive leave', 5, TRUE, TRUE, 1);

-- Insert Deduction Types
INSERT INTO deduction_types (type_name, description, is_mandatory, is_percentage, default_amount) VALUES
('SSS Contribution', 'Social Security System contribution', TRUE, TRUE, 0.045),
('PhilHealth Contribution', 'Philippine Health Insurance contribution', TRUE, TRUE, 0.025),
('Pag-IBIG Contribution', 'Home Development Mutual Fund contribution', TRUE, TRUE, 0.02),
('Withholding Tax', 'Income tax withheld from salary', TRUE, FALSE, 0.00),
('Late Deduction', 'Deduction for tardiness', FALSE, FALSE, 0.00),
('Undertime Deduction', 'Deduction for early departure', FALSE, FALSE, 0.00),
('Unpaid Leave Deduction', 'Deduction for unpaid leave days', FALSE, FALSE, 0.00),
('Uniform Deduction', 'Company uniform cost deduction', FALSE, FALSE, 500.00),
('Training Fee', 'Training and development fee', FALSE, FALSE, 1000.00),
('Equipment Damage', 'Cost of damaged company equipment', FALSE, FALSE, 0.00);

-- =====================================================
-- 3. INSERT EMPLOYEE DATA (ALIGNED WITH YOUR EXISTING DATA)
-- =====================================================

-- Insert Employees with proper department and position mapping
INSERT INTO employees (employee_id, last_name, first_name, birthday, address, phone_number, email, sss_number, philhealth_number, tin_number, pagibig_number, status_id, position_id, department_id, immediate_supervisor_id) VALUES
(10001, 'Garcia', 'Manuel III', '1983-10-11', 'Valero Carpark Building Valero Street 1227, Makati City', '966-860-270', 'manuel.garcia@motorph.com', '44-4506057-3', '820126853951', '442-605-657-000', '691295330870', 1, 1, 1, NULL),
(10002, 'Lim', 'Antonio', '1988-06-19', 'San Antonio De Padua 2, Block 1 Lot 8 and 2, Dasmarinas, Cavite', '171-867-411', 'antonio.lim@motorph.com', '52-2061274-9', '331735646338', '683-102-776-000', '663904995411', 1, 2, 2, 10001),
(10003, 'Aquino', 'Bianca Sofia', '1989-08-04', 'Rm. 402 4/F Jiao Building Timog Avenue Cor. Quezon Avenue 1100, Quezon City', '966-889-370', 'bianca.aquino@motorph.com', '30-8870406-2', '177451189665', '971-711-280-000', '171519773969', 1, 3, 3, 10001),
(10004, 'Reyes', 'Isabella', '1994-06-16', '460 Solanda Street Intramuros 1000, Manila', '786-868-477', 'isabella.reyes@motorph.com', '40-2511815-0', '341911411254', '876-809-437-000', '416946776041', 1, 4, 4, 10001),
(10005, 'Hernandez', 'Eduard', '1989-09-23', 'National Highway, Gingoog, Misamis Occidental', '088-861-012', 'eduard.hernandez@motorph.com', '50-5577638-1', '957436191812', '031-702-374-000', '952347222457', 1, 5, 5, 10002),
(10006, 'Villanueva', 'Andrea Mae', '1988-02-14', '17/85 Stracke Via Suite 042, Poblacion, Las Piñas 4783 Dinagat Islands', '918-621-603', 'andrea.villanueva@motorph.com', '49-1632020-8', '382189453145', '317-674-022-000', '441093369646', 1, 6, 6, 10002),
(10007, 'San Jose', 'Brad', '1996-03-15', '99 Strosin Hills, Poblacion, Bislig 5340 Tawi-Tawi', '797-009-261', 'brad.sanjose@motorph.com', '40-2400714-1', '239192926939', '672-474-690-000', '210850209964', 1, 7, 6, 10006),
(10008, 'Romualdez', 'Alice', '1992-05-14', '12A/33 Upton Isle Apt. 420, Roxas City 1814 Surigao del Norte', '983-606-799', 'alice.romualdez@motorph.com', '55-4476527-2', '545652640232', '888-572-294-000', '211385556888', 1, 8, 6, 10007),
(10009, 'Atienza', 'Rosie', '1948-09-24', '90A Dibbert Terrace Apt. 190, San Lorenzo 6056 Davao del Norte', '266-036-427', 'rosie.atienza@motorph.com', '41-0644692-3', '708988234853', '604-997-793-000', '260107732354', 1, 8, 6, 10007),
(10010, 'Alvaro', 'Roderick', '1988-03-30', '#284 T. Morato corner, Scout Rallos Street, Quezon City', '053-381-386', 'roderick.alvaro@motorph.com', '64-7605054-4', '578114853194', '525-420-419-000', '799254095212', 1, 9, 3, 10003),
(10011, 'Salcedo', 'Anthony', '1993-09-14', '93/54 Shanahan Alley Apt. 183, Santo Tomas 1572 Masbate', '070-766-300', 'anthony.salcedo@motorph.com', '26-9647608-3', '126445315651', '210-805-911-000', '218002473454', 1, 10, 3, 10010),
(10012, 'Lopez', 'Josie', '1987-01-14', '49 Springs Apt. 266, Poblacion, Taguig 3200 Occidental Mindoro', '478-355-427', 'josie.lopez@motorph.com', '44-8563448-3', '431709011012', '218-489-737-000', '113071293354', 1, 11, 3, 10011),
(10013, 'Farala', 'Martha', '1942-01-11', '42/25 Sawayn Stream, Ubay 1208 Zamboanga del Norte', '329-034-366', 'martha.farala@motorph.com', '45-5656375-0', '233693897247', '210-835-851-000', '631130283546', 1, 11, 3, 10011),
(10014, 'Martinez', 'Leila', '1970-07-11', '37/46 Kulas Roads, Maragondon 0962 Quirino', '877-110-749', 'leila.martinez@motorph.com', '27-2090996-4', '515741057496', '275-792-513-000', '101205445886', 1, 11, 3, 10011),
(10015, 'Romualdez', 'Fredrick', '1985-03-10', '22A/52 Lubowitz Meadows, Pililla 4895 Zambales', '023-079-009', 'fredrick.romualdez@motorph.com', '26-8768374-1', '308366860059', '598-065-761-000', '223057707853', 1, 12, 7, 10002),
(10016, 'Mata', 'Christian', '1987-10-21', '90 O''Keefe Spur Apt. 379, Catigbian 2772 Sulu', '783-776-744', 'christian.mata@motorph.com', '49-2959312-6', '824187961962', '103-100-522-000', '631052853464', 1, 13, 7, 10015),
(10017, 'De Leon', 'Selena', '1975-02-20', '89A Armstrong Trace, Compostela 7874 Maguindanao', '975-432-139', 'selena.deleon@motorph.com', '27-2090208-8', '587272469938', '482-259-498-000', '719007608464', 1, 13, 7, 10015),
(10018, 'San Jose', 'Allison', '1986-06-24', '08 Grant Drive Suite 406, Poblacion, Iloilo City 9186 La Union', '179-075-129', 'allison.sanjose@motorph.com', '45-3251383-0', '745148459521', '121-203-336-000', '114901859343', 1, 14, 9, 10015),
(10019, 'Rosales', 'Bradly', '1988-06-24', '92 Pagac Spur Suite 864, Poblacion, Tabuk 2412 Quirino', '797-009-261', 'bradly.rosales@motorph.com', '52-0423661-2', '364245235467', '317-674-022-000', '441093369646', 1, 15, 9, 10015),
(10020, 'Aquino', 'Jeremy', '1976-11-29', '75A/67 Gislason Glens Apt. 152, Bagong Silang, Caloocan 1400 Metro Manila', '491-046-555', 'jeremy.aquino@motorph.com', '30-8870406-2', '201710080255', '971-711-280-000', '171519773969', 1, 16, 8, 10002),
(10021, 'Cruz', 'Judson', '1966-08-27', '47A/85 Mohr Corners Suite 086, Poblacion, Basco 3900 Batanes', '329-034-366', 'judson.cruz@motorph.com', '45-5656375-0', '233693897247', '210-835-851-000', '631130283546', 1, 17, 2, 10002),
(10022, 'Mata', 'Andres', '1989-11-18', '90A/25 Gusikowski Rapid Apt. 179, Balamban 1208 Cebu', '877-110-749', 'andres.mata@motorph.com', '27-2090996-4', '515741057496', '275-792-513-000', '101205445886', 1, 18, 5, 10005),
(10023, 'Santos', 'Vella', '1983-12-31', '99A Padberg Spring, Poblacion, Mabalacat 3959 Lanao del Sur', '955-879-269', NULL, '52-9883524-3', '548670482885', '101-558-994-000', '360028104576', 2, 15, 9, 10018), -- Fixed: Added NULL for email
(10024, 'Del Rosario', 'Tomas', '1978-12-18', '80A/48 Ledner Ridges, Poblacion, Kabankalan 8870 Marinduque', '882-550-989', NULL, '45-5866331-6', '953901539995', '560-735-732-000', '913108649964', 2, 15, 9, 10018), -- Fixed
(10025, 'Tolentino', 'Jacklyn', '1984-05-19', '96/48 Watsica Flats Suite 734, Poblacion, Malolos 1844 Ifugao', '675-757-366', NULL, '47-1692793-0', '753800654114', '841-177-857-000', '210546661243', 2, 15, 9, 10017), -- Fixed
(10026, 'Gutierrez', 'Percival', '1970-12-18', '58A Wilderman Walks, Poblacion, Digos 5822 Davao del Sur', '512-899-876', NULL, '40-9504657-8', '797639382265', '502-995-671-000', '210897095686', 2, 15, 9, 10017), -- Fixed
(10027, 'Manalaysay', 'Garfield', '1986-08-28', '60 Goyette Valley Suite 219, Poblacion, Tabuk 3159 Lanao del Sur', '948-628-136', NULL, '45-3298166-4', '810909286264', '336-676-445-000', '211274476563', 2, 15, 9, 10017), -- Fixed
(10028, 'Villegas', 'Lizeth', '1981-12-12', '66/77 Mann Views, Luisiana 1263 Dinagat Islands', '332-372-215', NULL, '40-2400719-4', '934389652994', '210-395-397-000', '122238077997', 2, 15, 9, 10017), -- Fixed
(10029, 'Ramos', 'Carol', '1978-08-20', '72/70 Stamm Spurs, Bustos 4550 Iloilo', '250-700-389', NULL, '60-1152206-4', '351830469744', '395-032-717-000', '212141893454', 2, 15, 9, 10017), -- Fixed
(10030, 'Maceda', 'Emelia', '1973-04-14', '50A/83 Bahringer Oval Suite 145, Kiamba 7688 Nueva Ecija', '973-358-041', NULL, '54-1331005-0', '465087894112', '215-973-013-000', '515012579765', 2, 15, 9, 10017), -- Fixed
(10031, 'Aguilar', 'Delia', '1989-01-27', '95 Cremin Junction, Surallah 2809 Cotabato', '529-705-439', NULL, '52-1859253-1', '136451303068', '599-312-588-000', '110018813465', 2, 15, 9, 10017), -- Fixed
(10032, 'Castro', 'John', '1992-02-09', 'Hi-way, Yati, Liloan Cebu', '332-424-955', NULL, '26-7145133-4', '601644902402', '404-768-309-000', '697764069311', 1, 12, 7, 10004), -- Fixed
(10033, 'Martinez', 'Carlos', '1990-11-16', 'Bulala, Camalaniugan', '078-854-208', NULL, '11-5062972-7', '380685387212', '256-436-296-000', '993372963726', 1, 16, 8, 10004), -- Fixed
(10034, 'Santos', 'Beatriz', '1990-08-07', 'Agapita Building, Metro Manila', '526-639-511', NULL, '20-2987501-5', '918460050077', '911-529-713-000', '874042259378', 1, 14, 9, 10004); -- Fixed
-- =====================================================
-- 4. INSERT EMPLOYEE SALARY DATA
-- =====================================================

INSERT INTO employee_salary (employee_id, basic_salary, rice_subsidy, phone_allowance, clothing_allowance, gross_semimonthly_rate, hourly_rate, salary_grade, effective_date) VALUES
(10001, 200000.00, 1500.00, 2000.00, 1000.00, 100000.00, 1136.36, 'CEO', '2020-01-01'),
(10002, 150000.00, 1500.00, 2000.00, 1000.00, 75000.00, 852.27, 'C-1', '2020-01-01'),
(10003, 120000.00, 1500.00, 2000.00, 1000.00, 60000.00, 681.82, 'C-2', '2020-01-01'),
(10004, 100000.00, 1500.00, 2000.00, 1000.00, 50000.00, 568.18, 'C-3', '2020-01-01'),
(10005, 80000.00, 1500.00, 1500.00, 1000.00, 40000.00, 454.55, 'M-1', '2020-01-01'),
(10006, 60000.00, 1500.00, 1500.00, 1000.00, 30000.00, 340.91, 'M-2', '2020-01-01'),
(10007, 45000.00, 1500.00, 800.00, 1000.00, 22500.00, 255.68, 'S-1', '2020-01-01'),
(10008, 35000.00, 1500.00, 800.00, 1000.00, 17500.00, 198.86, 'S-2', '2020-01-01'),
(10009, 35000.00, 1500.00, 800.00, 1000.00, 17500.00, 198.86, 'S-2', '2020-01-01'),
(10010, 55000.00, 1500.00, 1200.00, 1000.00, 27500.00, 312.50, 'M-3', '2020-01-01'),
(10011, 50000.00, 1500.00, 1000.00, 1000.00, 25000.00, 284.09, 'M-4', '2020-01-01'),
(10012, 35000.00, 1500.00, 800.00, 1000.00, 17500.00, 198.86, 'S-3', '2020-01-01'),
(10013, 35000.00, 1500.00, 800.00, 1000.00, 17500.00, 198.86, 'S-3', '2020-01-01'),
(10014, 35000.00, 1500.00, 800.00, 1000.00, 17500.00, 198.86, 'S-3', '2020-01-01'),
(10015, 60000.00, 1500.00, 1200.00, 1000.00, 30000.00, 340.91, 'M-5', '2020-01-01'),
(10016, 30000.00, 1500.00, 800.00, 1000.00, 15000.00, 170.45, 'E-1', '2020-01-01'),
(10017, 30000.00, 1500.00, 800.00, 1000.00, 15000.00, 170.45, 'E-1', '2020-01-01'),
(10018, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05, 'E-2', '2020-01-01'),
(10019, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05, 'E-2', '2020-01-01'),
(10020, 40000.00, 1500.00, 1000.00, 1000.00, 20000.00, 227.27, 'S-4', '2020-01-01'),
(10021, 40000.00, 1500.00, 1000.00, 1000.00, 20000.00, 227.27, 'S-4', '2020-01-01'),
(10022, 45000.00, 1500.00, 1200.00, 1000.00, 22500.00, 255.68, 'S-5', '2020-01-01'),
(10023, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05, 'E-2', '2020-01-01'),
(10024, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05, 'E-2', '2020-01-01'),
(10025, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05, 'E-2', '2020-01-01'),
(10026, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05, 'E-2', '2020-01-01'),
(10027, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05, 'E-2', '2020-01-01'),
(10028, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05, 'E-2', '2020-01-01'),
(10029, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05, 'E-2', '2020-01-01'),
(10030, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05, 'E-2', '2020-01-01'),
(10031, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05, 'E-2', '2020-01-01'),
(10032, 60000.00, 1500.00, 1200.00, 1000.00, 30000.00, 340.91, 'M-5', '2020-01-01'),
(10033, 40000.00, 1500.00, 1000.00, 1000.00, 20000.00, 227.27, 'S-4', '2020-01-01'),
(10034, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05, 'E-2', '2020-01-01');

-- =====================================================
-- 5. CREDENTIALS TABLE - ESSENTIAL FOR LOGIN SYSTEM
-- =====================================================

-- Create credentials table to align with your CredentialsDAO
CREATE TABLE credentials (
    credential_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_password_change TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    password_expires_at TIMESTAMP NULL,
    failed_attempts INT DEFAULT 0,
    locked_until TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    INDEX idx_employee_credentials (employee_id),
    INDEX idx_credentials_active (is_active)
);

-- Insert default credentials (password1234 for all employees)
-- In production, these should be hashed and employees should change on first login
INSERT INTO credentials (employee_id, password) VALUES
(10001, 'password1234'),
(10002, 'password1234'),
(10003, 'password1234'),
(10004, 'password1234'),
(10005, 'password1234'),
(10006, 'password1234'),
(10007, 'password1234'),
(10008, 'password1234'),
(10009, 'password1234'),
(10010, 'password1234'),
(10011, 'password1234'),
(10012, 'password1234'),
(10013, 'password1234'),
(10014, 'password1234'),
(10015, 'password1234'),
(10016, 'password1234'),
(10017, 'password1234'),
(10018, 'password1234'),
(10019, 'password1234'),
(10020, 'password1234'),
(10021, 'password1234'),
(10022, 'password1234'),
(10023, 'password1234'),
(10024, 'password1234'),
(10025, 'password1234'),
(10026, 'password1234'),
(10027, 'password1234'),
(10028, 'password1234'),
(10029, 'password1234'),
(10030, 'password1234'),
(10031, 'password1234'),
(10032, 'password1234'),
(10033, 'password1234'),
(10034, 'password1234');

-- =====================================================
-- 6. ADDITIONAL TABLES TO ALIGN WITH YOUR CODE
-- =====================================================

-- Attendance table (matches your AttendanceDAO)
CREATE TABLE attendance (
    id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    date DATE NOT NULL,
    log_in TIME,
    log_out TIME,
    break_start TIME,
    break_end TIME,
    hours_worked DECIMAL(4,2) DEFAULT 0.00,
    overtime_hours DECIMAL(4,2) DEFAULT 0.00,
    undertime_hours DECIMAL(4,2) DEFAULT 0.00,
    late_minutes INT DEFAULT 0,
    early_out_minutes INT DEFAULT 0,
    attendance_status ENUM('Present', 'Absent', 'Late', 'Half_Day', 'On_Leave') DEFAULT 'Present',
    remarks TEXT,
    approved_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES employees(employee_id),
    UNIQUE KEY unique_employee_date (employee_id, date),
    INDEX idx_attendance_date (date),
    INDEX idx_employee_date (employee_id, date),
    INDEX idx_attendance_status (attendance_status)
);

-- Leave request table (matches your LeaveRequestDAO)
CREATE TABLE leave_request (
    leave_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    leave_type VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    days_requested DECIMAL(3,1) NOT NULL,
    reason TEXT,
    status ENUM('Pending', 'Approved', 'Rejected', 'Cancelled') DEFAULT 'Pending',
    approved_by INT,
    approved_date TIMESTAMP NULL,
    rejection_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES employees(employee_id),
    INDEX idx_employee_leave (employee_id),
    INDEX idx_leave_dates (start_date, end_date),
    INDEX idx_leave_status (status)
);

-- Overtime table (matches your OvertimeDAO)
CREATE TABLE overtime (
    overtime_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    date DATE NOT NULL,
    hours DECIMAL(4,2) NOT NULL,
    reason TEXT,
    approved BOOLEAN DEFAULT FALSE,
    approved_by INT,
    approved_date TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES employees(employee_id),
    INDEX idx_employee_overtime (employee_id),
    INDEX idx_overtime_date (date),
    INDEX idx_overtime_approved (approved)
);

-- Payroll table (matches your PayrollDAO)
CREATE TABLE payroll (
    payroll_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    monthly_rate DECIMAL(10,2) NOT NULL,
    days_worked INT DEFAULT 0,
    overtime_hours DECIMAL(6,2) DEFAULT 0.00,
    gross_pay DECIMAL(10,2) NOT NULL,
    total_deductions DECIMAL(10,2) DEFAULT 0.00,
    net_pay DECIMAL(10,2) NOT NULL,
    gross_earnings DECIMAL(10,2) DEFAULT 0.00,
    late_deduction DECIMAL(8,2) DEFAULT 0.00,
    undertime_deduction DECIMAL(8,2) DEFAULT 0.00,
    unpaid_leave_deduction DECIMAL(8,2) DEFAULT 0.00,
    overtime_pay DECIMAL(8,2) DEFAULT 0.00,
    rice_subsidy DECIMAL(8,2) DEFAULT 0.00,
    phone_allowance DECIMAL(8,2) DEFAULT 0.00,
    clothing_allowance DECIMAL(8,2) DEFAULT 0.00,
    sss DECIMAL(8,2) DEFAULT 0.00,
    philhealth DECIMAL(8,2) DEFAULT 0.00,
    pagibig DECIMAL(8,2) DEFAULT 0.00,
    tax DECIMAL(8,2) DEFAULT 0.00,
    status ENUM('draft', 'calculated', 'approved', 'paid') DEFAULT 'draft',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    UNIQUE KEY unique_employee_period (employee_id, period_start, period_end),
    INDEX idx_payroll_period (period_start, period_end),
    INDEX idx_payroll_employee (employee_id),
    INDEX idx_payroll_status (status)
);

-- =====================================================
-- 7. SAMPLE DATA FOR TESTING
-- =====================================================

-- Sample attendance records
INSERT INTO attendance (employee_id, date, log_in, log_out, hours_worked, attendance_status) VALUES
(10001, '2024-12-01', '08:00:00', '17:00:00', 8.00, 'Present'),
(10001, '2024-12-02', '08:15:00', '17:00:00', 7.75, 'Late'),
(10002, '2024-12-01', '07:45:00', '17:30:00', 8.75, 'Present'),
(10002, '2024-12-02', '08:00:00', '17:00:00', 8.00, 'Present'),
(10003, '2024-12-01', '08:30:00', '16:30:00', 7.50, 'Late'),
(10003, '2024-12-02', '08:00:00', '17:00:00', 8.00, 'Present');

-- Sample leave requests
INSERT INTO leave_request (employee_id, leave_type, start_date, end_date, days_requested, reason, status) VALUES
(10001, 'Annual', '2024-12-20', '2024-12-22', 3, 'Christmas vacation', 'Approved'),
(10002, 'Sick', '2024-12-15', '2024-12-15', 1, 'Medical appointment', 'Pending'),
(10003, 'Emergency', '2024-12-10', '2024-12-11', 2, 'Family emergency', 'Approved');

-- =====================================================
-- 8. VIEWS FOR REPORTING (OPTIONAL)
-- =====================================================

-- Employee summary view
CREATE VIEW employee_summary AS
SELECT 
    e.employee_id,
    CONCAT(e.first_name, ' ', e.last_name) AS full_name,
    p.position_title,
    d.department_name,
    es.basic_salary,
    es.gross_semimonthly_rate,
    e.status,
    e.date_hired
FROM employees e
LEFT JOIN positions p ON e.position_id = p.position_id
LEFT JOIN departments d ON e.department_id = d.department_id
LEFT JOIN employee_salary es ON e.employee_id = es.employee_id AND es.is_active = TRUE;

-- Payroll summary view
CREATE VIEW payroll_summary AS
SELECT 
    p.payroll_id,
    p.employee_id,
    CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
    p.period_start,
    p.period_end,
    p.gross_pay,
    p.total_deductions,
    p.net_pay,
    p.status
FROM payroll p
JOIN employees e ON p.employee_id = e.employee_id;

-- =====================================================
-- 9. STORED PROCEDURES (OPTIONAL)
-- =====================================================

- =====================================================
-- CRUD OPERATIONS EXAMPLES
-- =====================================================

-- =====================================================
-- 1. CREATE (ADD) NEW EMPLOYEE
-- =====================================================

-- Example: Add a new employee
INSERT INTO employees (
    employee_id, 
    last_name, 
    first_name, 
    middle_name,
    birthday, 
    address, 
    phone_number, 
    email,
    sss_number, 
    philhealth_number, 
    tin_number, 
    pagibig_number, 
    status_id, 
    position_id, 
    department_id, 
    immediate_supervisor_id,
    date_hired
) VALUES (
    10035, 
    'Rodriguez', 
    'Maria', 
    'Santos',
    '1995-05-20', 
    '123 Main Street, Quezon City', 
    '912-345-678', 
    'maria.rodriguez@motorph.com',
    '55-1234567-8', 
    '123456789012', 
    '123-456-789-000', 
    '123456789012', 
    1, -- Regular status
    15, -- Customer Service Representative position
    9, -- Customer Service department
    10018, -- Reports to Allison San Jose
    CURDATE()
);

-- Add salary information for the new employee
INSERT INTO employee_salary (
    employee_id, 
    basic_salary, 
    rice_subsidy, 
    phone_allowance, 
    clothing_allowance, 
    gross_semimonthly_rate, 
    hourly_rate, 
    salary_grade,
    effective_date
) VALUES (
    10035, 
    28000.00, 
    1500.00, 
    800.00, 
    1000.00, 
    14000.00, 
    159.09, 
    'E-3',
    CURDATE()
);

-- Add credentials for the new employee
INSERT INTO credentials (employee_id, password) VALUES (10035, 'password1234');

-- =====================================================
-- 2. READ (RETRIEVE) EMPLOYEE DATA
-- =====================================================

-- Get all employee information with their positions and departments
SELECT 
    e.employee_id,
    CONCAT(e.first_name, ' ', IFNULL(e.middle_name, ''), ' ', e.last_name) AS full_name,
    e.birthday,
    e.address,
    e.phone_number,
    e.email,
    p.position_title,
    d.department_name,
    est.status_name,
    e.date_hired,
    es.basic_salary,
    es.gross_semimonthly_rate
FROM employees e
LEFT JOIN positions p ON e.position_id = p.position_id
LEFT JOIN departments d ON e.department_id = d.department_id
LEFT JOIN employee_status_types est ON e.status_id = est.status_id
LEFT JOIN employee_salary es ON e.employee_id = es.employee_id AND es.is_active = TRUE
WHERE e.is_active = TRUE
ORDER BY e.employee_id;

-- Get specific employee by ID
SELECT 
    e.*,
    p.position_title,
    d.department_name,
    est.status_name,
    es.basic_salary,
    CONCAT(sup.first_name, ' ', sup.last_name) AS supervisor_name
FROM employees e
LEFT JOIN positions p ON e.position_id = p.position_id
LEFT JOIN departments d ON e.department_id = d.department_id
LEFT JOIN employee_status_types est ON e.status_id = est.status_id
LEFT JOIN employee_salary es ON e.employee_id = es.employee_id AND es.is_active = TRUE
LEFT JOIN employees sup ON e.immediate_supervisor_id = sup.employee_id
WHERE e.employee_id = 10001;

-- =====================================================
-- 3. UPDATE EMPLOYEE DATA
-- =====================================================

-- Update employee basic information
UPDATE employees 
SET 
    phone_number = '999-888-777',
    email = 'maria.rodriguez.updated@motorph.com',
    address = '456 Updated Street, Manila',
    updated_at = CURRENT_TIMESTAMP
WHERE employee_id = 10035;

-- Update employee position/department
UPDATE employees 
SET 
    position_id = 13, -- Change to Sales Representative
    department_id = 7, -- Move to Sales department
    immediate_supervisor_id = 10015, -- New supervisor
    updated_at = CURRENT_TIMESTAMP
WHERE employee_id = 10035;

-- Update salary information (create new active record, deactivate old one)
UPDATE employee_salary 
SET 
    is_active = FALSE,
    end_date = CURDATE(),
    updated_at = CURRENT_TIMESTAMP
WHERE employee_id = 10035 AND is_active = TRUE;

INSERT INTO employee_salary (
    employee_id, 
    basic_salary, 
    rice_subsidy, 
    phone_allowance, 
    clothing_allowance, 
    gross_semimonthly_rate, 
    hourly_rate, 
    salary_grade,
    effective_date
) VALUES (
    10035, 
    32000.00, -- Increased salary
    1500.00, 
    1000.00, -- Increased phone allowance
    1000.00, 
    16000.00, 
    181.82, 
    'E-4',
    CURDATE()
);

-- =====================================================
-- 4. DELETE EMPLOYEE (SOFT DELETE - RECOMMENDED)
-- =====================================================

-- Soft delete - Mark employee as inactive (RECOMMENDED approach)
UPDATE employees 
SET 
    is_active = FALSE,
    date_terminated = CURDATE(),
    termination_reason = 'Resignation',
    updated_at = CURRENT_TIMESTAMP
WHERE employee_id = 10035;

-- Also deactivate their salary record
UPDATE employee_salary 
SET 
    is_active = FALSE,
    end_date = CURDATE(),
    updated_at = CURRENT_TIMESTAMP
WHERE employee_id = 10035 AND is_active = TRUE;

-- Deactivate their credentials
UPDATE credentials 
SET 
    is_active = FALSE,
    updated_at = CURRENT_TIMESTAMP
WHERE employee_id = 10035;

-- =====================================================
-- 5. HARD DELETE (USE WITH CAUTION)
-- =====================================================

-- Hard delete - Permanently remove from database (USE WITH EXTREME CAUTION)
-- This will cascade delete related records due to foreign key constraints

-- First, you might want to backup the data before deleting
-- CREATE TABLE deleted_employees_backup AS SELECT * FROM employees WHERE employee_id = 10035;
-- CREATE TABLE deleted_salary_backup AS SELECT * FROM employee_salary WHERE employee_id = 10035;

-- Delete employee (this will cascade to related tables due to ON DELETE CASCADE)
-- DELETE FROM employees WHERE employee_id = 10035;

-- =====================================================
-- 6. BULK OPERATIONS
-- =====================================================

-- Add multiple employees at once
INSERT INTO employees (
    employee_id, last_name, first_name, birthday, address, phone_number, 
    email, sss_number, philhealth_number, tin_number, pagibig_number, 
    status_id, position_id, department_id, immediate_supervisor_id
) VALUES 
(10036, 'Brown', 'James', '1990-01-15', '789 Oak Street, Makati', '911-222-333', 'james.brown@motorph.com', '11-1111111-1', '111111111111', '111-111-111-000', '111111111111', 1, 15, 9, 10018),
(10037, 'White', 'Sarah', '1992-03-22', '321 Pine Avenue, Pasig', '922-333-444', 'sarah.white@motorph.com', '22-2222222-2', '222222222222', '222-222-222-000', '222222222222', 2, 15, 9, 10018);

-- Update multiple employees' status
UPDATE employees 
SET 
    status_id = 1, -- Change from Probationary to Regular
    updated_at = CURRENT_TIMESTAMP
WHERE status_id = 2 AND DATEDIFF(CURDATE(), date_hired) >= 180; -- 6 months probation

-- Delete multiple inactive employees
DELETE FROM employees 
WHERE is_active = FALSE 
  AND date_terminated IS NOT NULL 
  AND DATEDIFF(CURDATE(), date_terminated) > 2555; -- 7 years retention

-- =====================================================
-- 7. VERIFICATION QUERIES
-- =====================================================

-- Count total active employees
SELECT COUNT(*) as total_active_employees FROM employees WHERE is_active = TRUE;

-- Count employees by department
SELECT 
    d.department_name,
    COUNT(e.employee_id) as employee_count
FROM departments d
LEFT JOIN employees e ON d.department_id = e.department_id AND e.is_active = TRUE
GROUP BY d.department_id, d.department_name
ORDER BY employee_count DESC;

-- Count employees by status
SELECT 
    est.status_name,
    COUNT(e.employee_id) as employee_count
FROM employee_status_types est
LEFT JOIN employees e ON est.status_id = e.status_id AND e.is_active = TRUE
GROUP BY est.status_id, est.status_name
ORDER BY employee_count DESC;

-- Verify data integrity (employees without salary records)
SELECT 
    e.employee_id,
    CONCAT(e.first_name, ' ', e.last_name) as employee_name
FROM employees e
LEFT JOIN employee_salary es ON e.employee_id = es.employee_id AND es.is_active = TRUE
WHERE e.is_active = TRUE AND es.employee_id IS NULL;

-- =====================================================
-- 8. STORED PROCEDURES FOR EMPLOYEE MANAGEMENT
-- =====================================================

DELIMITER //

-- Procedure to add new employee with all related data
CREATE PROCEDURE AddNewEmployee(
    IN p_employee_id INT,
    IN p_last_name VARCHAR(100),
    IN p_first_name VARCHAR(100),
    IN p_middle_name VARCHAR(100),
    IN p_birthday DATE,
    IN p_address TEXT,
    IN p_phone_number VARCHAR(20),
    IN p_email VARCHAR(100),
    IN p_sss_number VARCHAR(20),
    IN p_philhealth_number VARCHAR(20),
    IN p_tin_number VARCHAR(20),
    IN p_pagibig_number VARCHAR(20),
    IN p_status_id INT,
    IN p_position_id INT,
    IN p_department_id INT,
    IN p_immediate_supervisor_id INT,
    IN p_basic_salary DECIMAL(10,2)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- Insert employee
    INSERT INTO employees (
        employee_id, last_name, first_name, middle_name, birthday, address, 
        phone_number, email, sss_number, philhealth_number, tin_number, 
        pagibig_number, status_id, position_id, department_id, 
        immediate_supervisor_id, date_hired
    ) VALUES (
        p_employee_id, p_last_name, p_first_name, p_middle_name, p_birthday, 
        p_address, p_phone_number, p_email, p_sss_number, p_philhealth_number, 
        p_tin_number, p_pagibig_number, p_status_id, p_position_id, 
        p_department_id, p_immediate_supervisor_id, CURDATE()
    );
    
    -- Insert salary
    INSERT INTO employee_salary (
        employee_id, basic_salary, rice_subsidy, phone_allowance, 
        clothing_allowance, gross_semimonthly_rate, hourly_rate, effective_date
    ) VALUES (
        p_employee_id, p_basic_salary, 1500.00, 800.00, 1000.00, 
        p_basic_salary / 2, p_basic_salary / 176, CURDATE()
    );
    
    -- Insert credentials
    INSERT INTO credentials (employee_id, password) VALUES (p_employee_id, 'password1234');
    
    COMMIT;
    
    SELECT 'Employee added successfully' as result;
END //

-- Procedure to soft delete employee
CREATE PROCEDURE SoftDeleteEmployee(
    IN p_employee_id INT,
    IN p_termination_reason TEXT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- Update employee status
    UPDATE employees 
    SET 
        is_active = FALSE,
        date_terminated = CURDATE(),
        termination_reason = p_termination_reason,
        updated_at = CURRENT_TIMESTAMP
    WHERE employee_id = p_employee_id;
    
    -- Deactivate salary
    UPDATE employee_salary 
    SET 
        is_active = FALSE,
        end_date = CURDATE(),
        updated_at = CURRENT_TIMESTAMP
    WHERE employee_id = p_employee_id AND is_active = TRUE;
    
    -- Deactivate credentials
    UPDATE credentials 
    SET 
        is_active = FALSE,
        updated_at = CURRENT_TIMESTAMP
    WHERE employee_id = p_employee_id;
    
    COMMIT;
    
    SELECT 'Employee deactivated successfully' as result;
END //

DELIMITER ;

-- =====================================================
-- USAGE EXAMPLES OF STORED PROCEDURES
-- =====================================================

-- Add new employee using stored procedure
-- CALL AddNewEmployee(10038, 'Johnson', 'Michael', 'Ray', '1988-07-12', '555 Test Street', '933-444-555', 'michael.johnson@motorph.com', '33-3333333-3', '333333333333', '333-333-333-000', '333333333333', 1, 15, 9, 10018, 30000.00);

-- Soft delete employee using stored procedure
-- CALL SoftDeleteEmployee(10038, 'End of contract');


DELIMITER //

-- Procedure to get employee payroll summary
CREATE PROCEDURE GetEmployeePayrollSummary(IN emp_id INT, IN start_date DATE, IN end_date DATE)
BEGIN
    SELECT 
        e.employee_id,
        CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
        COUNT(a.id) AS attendance_days,
        COALESCE(SUM(a.hours_worked), 0) AS total_hours,
        COALESCE(SUM(o.hours), 0) AS overtime_hours,
        COALESCE(SUM(p.gross_pay), 0) AS total_gross_pay,
        COALESCE(SUM(p.net_pay), 0) AS total_net_pay
    FROM employees e
    LEFT JOIN attendance a ON e.employee_id = a.employee_id 
        AND a.date BETWEEN start_date AND end_date
    LEFT JOIN overtime o ON e.employee_id = o.employee_id 
        AND o.date BETWEEN start_date AND end_date AND o.approved = TRUE
    LEFT JOIN payroll p ON e.employee_id = p.employee_id 
        AND p.period_start >= start_date AND p.period_end <= end_date
    WHERE e.employee_id = emp_id
    GROUP BY e.employee_id;
END //

DELIMITER ;

-- =====================================================
-- 10. INDEXES FOR PERFORMANCE
-- =====================================================

-- Additional indexes for better query performance
CREATE INDEX idx_employees_name ON employees(last_name, first_name);
CREATE INDEX idx_employees_status ON employees(status);
CREATE INDEX idx_employees_hire_date ON employees(date_hired);
CREATE INDEX idx_salary_effective_date ON employee_salary(effective_date);
CREATE INDEX idx_attendance_employee_month ON attendance(employee_id, date);
CREATE INDEX idx_payroll_employee_period ON payroll(employee_id, period_start, period_end);

-- =====================================================
-- SETUP COMPLETE
-- =====================================================

-- Verify the setup
SELECT 'Database setup completed successfully!' AS status;
SELECT COUNT(*) AS total_employees FROM employees;
SELECT COUNT(*) AS total_credentials FROM credentials;
SELECT COUNT(*) AS total_departments FROM departments;
SELECT COUNT(*) AS total_positions FROM positions;
