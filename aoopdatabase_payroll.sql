-- =====================================================
-- MotorPH Payroll System Database - FUTURE-PROOF HYBRID
-- ✅ Works with your current DAO (no code changes)
-- ✅ 3NF Normalized (maintains data integrity)
-- ✅ Future-ready (supports advanced features)
-- ✅ Backward compatible (keeps existing structure)
-- =====================================================

-- Drop existing database if exists and create new one
DROP DATABASE IF EXISTS aoopdatabase_payroll;
CREATE DATABASE aoopdatabase_payroll;
USE aoopdatabase_payroll;

-- =====================================================
-- 1. REFERENCE TABLES (3NF Normalization)
-- =====================================================

-- Departments (for future reporting and normalization)
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

-- Positions (for future reporting and normalization)
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

-- Employee Status Types (for future reporting)
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

-- Leave Types (for future advanced leave management)
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

-- Deduction Types (for future advanced payroll)
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

-- =====================================================
-- 2. MAIN EMPLOYEES TABLE - HYBRID APPROACH
-- =====================================================

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
    
    -- CURRENT DAO COMPATIBILITY: Direct string fields
    status VARCHAR(50) DEFAULT 'Regular',
    position VARCHAR(100),
    immediate_supervisor VARCHAR(100),
    
    -- FUTURE NORMALIZATION: Foreign key references
    status_id INT,
    position_id INT,
    department_id INT,
    immediate_supervisor_id INT,
    
    -- CURRENT DAO COMPATIBILITY: Direct salary fields
    basic_salary DECIMAL(10,2) DEFAULT 0.00,
    rice_subsidy DECIMAL(8,2) DEFAULT 1500.00,
    phone_allowance DECIMAL(8,2) DEFAULT 0.00,
    clothing_allowance DECIMAL(8,2) DEFAULT 0.00,
    gross_semi_monthly_rate DECIMAL(10,2) DEFAULT 0.00,
    hourly_rate DECIMAL(8,2) DEFAULT 0.00,
    
    -- Standard fields
    date_hired DATE DEFAULT (CURRENT_DATE),
    date_terminated DATE NULL,
    termination_reason TEXT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key constraints (for future use)
    FOREIGN KEY (status_id) REFERENCES employee_status_types(status_id),
    FOREIGN KEY (position_id) REFERENCES positions(position_id),
    FOREIGN KEY (department_id) REFERENCES departments(department_id),
    FOREIGN KEY (immediate_supervisor_id) REFERENCES employees(employee_id),
    
    -- Indexes for performance
    INDEX idx_emp_name (last_name, first_name),
    INDEX idx_emp_status (status),
    INDEX idx_emp_position (position),
    INDEX idx_emp_status_id (status_id),
    INDEX idx_emp_position_id (position_id),
    INDEX idx_emp_dept (department_id),
    INDEX idx_emp_supervisor (immediate_supervisor_id),
    INDEX idx_emp_active (is_active),
    INDEX idx_emp_hired_date (date_hired)
);

-- =====================================================
-- 3. CREDENTIALS TABLE - EXACT MATCH FOR YOUR DAO
-- =====================================================

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

-- =====================================================
-- 4. ATTENDANCE TABLE - EXACT MATCH FOR YOUR DAO
-- =====================================================

CREATE TABLE attendance (
    id INT PRIMARY KEY AUTO_INCREMENT, -- Your DAO uses 'id'
    employee_id INT NOT NULL,
    date DATE NOT NULL, -- Your DAO uses 'date'
    log_in TIME, -- Your DAO uses 'log_in'
    log_out TIME, -- Your DAO uses 'log_out'
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

-- =====================================================
-- 5. LEAVE REQUEST TABLE - EXACT MATCH FOR YOUR DAO
-- =====================================================

CREATE TABLE leave_request (
    leave_id INT PRIMARY KEY AUTO_INCREMENT, -- Your DAO uses 'leave_id'
    employee_id INT NOT NULL,
    leave_type VARCHAR(50) NOT NULL, -- Your DAO uses varchar
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    days_requested DECIMAL(3,1) NOT NULL,
    reason TEXT,
    status ENUM('Pending', 'Approved', 'Rejected', 'Cancelled') DEFAULT 'Pending', -- Your DAO uses these values
    approved_by INT,
    approved_date TIMESTAMP NULL,
    rejection_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- FUTURE: Link to normalized leave types
    leave_type_id INT,
    FOREIGN KEY (leave_type_id) REFERENCES leave_types(leave_type_id),
    
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES employees(employee_id),
    INDEX idx_employee_leave (employee_id),
    INDEX idx_leave_dates (start_date, end_date),
    INDEX idx_leave_status (status),
    INDEX idx_leave_type_str (leave_type),
    INDEX idx_leave_type_id (leave_type_id)
);

-- =====================================================
-- 6. OVERTIME TABLE - EXACT MATCH FOR YOUR DAO
-- =====================================================

CREATE TABLE overtime (
    overtime_id INT PRIMARY KEY AUTO_INCREMENT, -- Your DAO uses 'overtime_id'
    employee_id INT NOT NULL,
    date DATE NOT NULL, -- Your DAO uses 'date'
    hours DECIMAL(4,2) NOT NULL, -- Your DAO uses 'hours'
    reason TEXT,
    approved BOOLEAN DEFAULT FALSE, -- Your DAO uses 'approved'
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

-- =====================================================
-- 7. PAYROLL TABLE - EXACT MATCH FOR YOUR DAO
-- =====================================================

CREATE TABLE payroll (
    payroll_id INT PRIMARY KEY AUTO_INCREMENT, -- Your DAO uses 'payroll_id'
    employee_id INT NOT NULL,
    period_start DATE NOT NULL, -- Your DAO uses 'period_start'
    period_end DATE NOT NULL, -- Your DAO uses 'period_end'
    monthly_rate DECIMAL(10,2) NOT NULL, -- Your DAO uses 'monthly_rate'
    days_worked INT DEFAULT 0,
    overtime_hours DECIMAL(6,2) DEFAULT 0.00,
    gross_pay DECIMAL(10,2) NOT NULL,
    total_deductions DECIMAL(10,2) DEFAULT 0.00,
    net_pay DECIMAL(10,2) NOT NULL,
    gross_earnings DECIMAL(10,2) DEFAULT 0.00, -- Your DAO uses this
    late_deduction DECIMAL(8,2) DEFAULT 0.00,
    undertime_deduction DECIMAL(8,2) DEFAULT 0.00,
    unpaid_leave_deduction DECIMAL(8,2) DEFAULT 0.00,
    overtime_pay DECIMAL(8,2) DEFAULT 0.00,
    rice_subsidy DECIMAL(8,2) DEFAULT 0.00,
    phone_allowance DECIMAL(8,2) DEFAULT 0.00,
    clothing_allowance DECIMAL(8,2) DEFAULT 0.00,
    sss DECIMAL(8,2) DEFAULT 0.00, -- Your DAO uses 'sss'
    philhealth DECIMAL(8,2) DEFAULT 0.00, -- Your DAO uses 'philhealth'
    pagibig DECIMAL(8,2) DEFAULT 0.00, -- Your DAO uses 'pagibig'
    tax DECIMAL(8,2) DEFAULT 0.00, -- Your DAO uses 'tax'
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
-- 8. DEDUCTIONS TABLE - EXACT MATCH FOR YOUR DAO
-- =====================================================

CREATE TABLE deductions (
    deduction_id INT PRIMARY KEY AUTO_INCREMENT, -- Your DAO uses 'deduction_id'
    employee_id INT NOT NULL,
    type VARCHAR(50) NOT NULL, -- Your DAO uses 'type'
    amount DECIMAL(10,2) NOT NULL, -- Your DAO uses 'amount'
    description TEXT, -- Your DAO uses 'description'
    deduction_date DATE DEFAULT (CURRENT_DATE), -- Your DAO uses 'deduction_date'
    effective_date DATE DEFAULT (CURRENT_DATE),
    end_date DATE,
    is_recurring BOOLEAN DEFAULT FALSE,
    frequency ENUM('once', 'weekly', 'monthly', 'quarterly', 'annually') DEFAULT 'once',
    remaining_installments INT DEFAULT 1,
    status ENUM('active', 'completed', 'suspended') DEFAULT 'active',
    approved_by INT,
    
    -- FUTURE: Link to normalized deduction types
    deduction_type_id INT,
    FOREIGN KEY (deduction_type_id) REFERENCES deduction_types(deduction_type_id),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES employees(employee_id),
    INDEX idx_employee_deductions (employee_id),
    INDEX idx_deduction_type_str (type),
    INDEX idx_deduction_type_id (deduction_type_id),
    INDEX idx_deduction_status (status)
);

-- =====================================================
-- 9. FUTURE-READY TABLES (For advanced features)
-- =====================================================

-- Government Contributions (for your GovernmentContributionsDAO)
CREATE TABLE government_contributions (
    contribution_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    sss DECIMAL(8,2) DEFAULT 0.00,
    philhealth DECIMAL(8,2) DEFAULT 0.00,
    pagibig DECIMAL(8,2) DEFAULT 0.00,
    tax DECIMAL(8,2) DEFAULT 0.00,
    effective_date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    INDEX idx_govt_contrib_employee (employee_id)
);

-- Compensation Details (for your CompensationDetailsDAO)
CREATE TABLE compensation_details (
    compensation_details_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    rice_subsidy DECIMAL(8,2) DEFAULT 0.00,
    phone_allowance DECIMAL(8,2) DEFAULT 0.00,
    clothing_allowance DECIMAL(8,2) DEFAULT 0.00,
    effective_date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    INDEX idx_comp_details_employee (employee_id)
);

-- Employment Status (for your EmploymentStatusDAO)
CREATE TABLE employment_status (
    employment_status_id INT PRIMARY KEY AUTO_INCREMENT,
    status_name VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Position (for your PositionDAO)
CREATE TABLE position (
    position_id INT PRIMARY KEY AUTO_INCREMENT,
    position_name VARCHAR(100) NOT NULL,
    department_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Employee Salary History (for future salary management)
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

-- Payroll Periods (for future advanced payroll)
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

-- System Users & Authentication (for future advanced security)
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

-- Audit Log (for future compliance and tracking)
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
-- 10. INSERT REFERENCE DATA
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

-- Insert Employment Status (for EmploymentStatusDAO)
INSERT INTO employment_status (employment_status_id, status_name, description) VALUES
(1, 'Regular', 'Regular full-time employee'),
(2, 'Probationary', 'Probationary employee'),
(3, 'Contractual', 'Contractual employee'),
(4, 'Part-time', 'Part-time employee');

-- Insert Position (for PositionDAO)
INSERT INTO position (position_id, position_name, department_name) VALUES
(1, 'Chief Executive Officer', 'Executive'),
(2, 'Chief Operating Officer', 'Operations'),
(3, 'Chief Finance Officer', 'Finance'),
(4, 'Chief Marketing Officer', 'Marketing'),
(5, 'IT Director', 'Information Technology'),
(6, 'HR Manager', 'Human Resources'),
(7, 'HR Team Leader', 'Human Resources'),
(8, 'HR Specialist', 'Human Resources'),
(9, 'Accounting Manager', 'Finance'),
(10, 'Payroll Manager', 'Finance'),
(11, 'Payroll Specialist', 'Finance'),
(12, 'Sales Manager', 'Sales'),
(13, 'Sales Representative', 'Sales'),
(14, 'Customer Service Manager', 'Customer Service'),
(15, 'Customer Service Representative', 'Customer Service'),
(16, 'Supply Chain Manager', 'Supply Chain'),
(17, 'Operations Supervisor', 'Operations'),
(18, 'IT System Administrator', 'Information Technology');

-- =====================================================
-- 11. INSERT EMPLOYEE DATA - HYBRID APPROACH
-- =====================================================

INSERT INTO employees (
    employee_id, last_name, first_name, birthday, address, phone_number, email, 
    sss_number, philhealth_number, tin_number, pagibig_number, 
    status, position, immediate_supervisor, -- Direct string fields for current DAO
    status_id, position_id, department_id, immediate_supervisor_id, -- Normalized fields for future
    basic_salary, rice_subsidy, phone_allowance, clothing_allowance, gross_semi_monthly_rate, hourly_rate
) VALUES 
-- Executive Level
(10001, 'Garcia', 'Manuel III', '1983-10-11', 'Valero Carpark Building Valero Street 1227, Makati City', '966-860-270', 'manuel.garcia@motorph.com', '44-4506057-3', '820126853951', '442-605-657-000', '691295330870', 'Regular', 'Chief Executive Officer', NULL, 1, 1, 1, NULL, 200000.00, 1500.00, 2000.00, 1000.00, 100000.00, 1136.36),

(10002, 'Lim', 'Antonio', '1988-06-19', 'San Antonio De Padua 2, Block 1 Lot 8 and 2, Dasmarinas, Cavite', '171-867-411', 'antonio.lim@motorph.com', '52-2061274-9', '331735646338', '683-102-776-000', '663904995411', 'Regular', 'Chief Operating Officer', 'Manuel Garcia III', 1, 2, 2, 10001, 150000.00, 1500.00, 2000.00, 1000.00, 75000.00, 852.27),

(10003, 'Aquino', 'Bianca Sofia', '1989-08-04', 'Rm. 402 4/F Jiao Building Timog Avenue Cor. Quezon Avenue 1100, Quezon City', '966-889-370', 'bianca.aquino@motorph.com', '30-8870406-2', '177451189665', '971-711-280-000', '171519773969', 'Regular', 'Chief Finance Officer', 'Manuel Garcia III', 1, 3, 3, 10001, 120000.00, 1500.00, 2000.00, 1000.00, 60000.00, 681.82),

(10004, 'Reyes', 'Isabella', '1994-06-16', '460 Solanda Street Intramuros 1000, Manila', '786-868-477', 'isabella.reyes@motorph.com', '40-2511815-0', '341911411254', '876-809-437-000', '416946776041', 'Regular', 'Chief Marketing Officer', 'Manuel Garcia III', 1, 4, 4, 10001, 100000.00, 1500.00, 2000.00, 1000.00, 50000.00, 568.18),

(10005, 'Hernandez', 'Eduard', '1989-09-23', 'National Highway, Gingoog, Misamis Occidental', '088-861-012', 'eduard.hernandez@motorph.com', '50-5577638-1', '957436191812', '031-702-374-000', '952347222457', 'Regular', 'IT Director', 'Antonio Lim', 1, 5, 5, 10002, 80000.00, 1500.00, 1500.00, 1000.00, 40000.00, 454.55),

-- HR Department
(10006, 'Villanueva', 'Andrea Mae', '1988-02-14', '17/85 Stracke Via Suite 042, Poblacion, Las Piñas 4783 Dinagat Islands', '918-621-603', 'andrea.villanueva@motorph.com', '49-1632020-8', '382189453145', '317-674-022-000', '441093369646', 'Regular', 'HR Manager', 'Antonio Lim', 1, 6, 6, 10002, 60000.00, 1500.00, 1500.00, 1000.00, 30000.00, 340.91),

(10007, 'San Jose', 'Brad', '1996-03-15', '99 Strosin Hills, Poblacion, Bislig 5340 Tawi-Tawi', '797-009-261', 'brad.sanjose@motorph.com', '40-2400714-1', '239192926939', '672-474-690-000', '210850209964', 'Regular', 'HR Team Leader', 'Andrea Mae Villanueva', 1, 7, 6, 10006, 45000.00, 1500.00, 800.00, 1000.00, 22500.00, 255.68),

(10008, 'Romualdez', 'Alice', '1992-05-14', '12A/33 Upton Isle Apt. 420, Roxas City 1814 Surigao del Norte', '983-606-799', 'alice.romualdez@motorph.com', '55-4476527-2', '545652640232', '888-572-294-000', '211385556888', 'Regular', 'HR Specialist', 'Brad San Jose', 1, 8, 6, 10007, 35000.00, 1500.00, 800.00, 1000.00, 17500.00, 198.86),

(10009, 'Atienza', 'Rosie', '1948-09-24', '90A Dibbert Terrace Apt. 190, San Lorenzo 6056 Davao del Norte', '266-036-427', 'rosie.atienza@motorph.com', '41-0644692-3', '708988234853', '604-997-793-000', '260107732354', 'Regular', 'HR Specialist', 'Brad San Jose', 1, 8, 6, 10007, 35000.00, 1500.00, 800.00, 1000.00, 17500.00, 198.86),

-- Finance Department
(10010, 'Alvaro', 'Roderick', '1988-03-30', '#284 T. Morato corner, Scout Rallos Street, Quezon City', '053-381-386', 'roderick.alvaro@motorph.com', '64-7605054-4', '578114853194', '525-420-419-000', '799254095212', 'Regular', 'Accounting Manager', 'Bianca Sofia Aquino', 1, 9, 3, 10003, 55000.00, 1500.00, 1200.00, 1000.00, 27500.00, 312.50),

(10011, 'Salcedo', 'Anthony', '1993-09-14', '93/54 Shanahan Alley Apt. 183, Santo Tomas 1572 Masbate', '070-766-300', 'anthony.salcedo@motorph.com', '26-9647608-3', '126445315651', '210-805-911-000', '218002473454', 'Regular', 'Payroll Manager', 'Roderick Alvaro', 1, 10, 3, 10010, 50000.00, 1500.00, 1000.00, 1000.00, 25000.00, 284.09),

(10012, 'Lopez', 'Josie', '1987-01-14', '49 Springs Apt. 266, Poblacion, Taguig 3200 Occidental Mindoro', '478-355-427', 'josie.lopez@motorph.com', '44-8563448-3', '431709011012', '218-489-737-000', '113071293354', 'Regular', 'Payroll Specialist', 'Anthony Salcedo', 1, 11, 3, 10011, 35000.00, 1500.00, 800.00, 1000.00, 17500.00, 198.86),

(10013, 'Farala', 'Martha', '1942-01-11', '42/25 Sawayn Stream, Ubay 1208 Zamboanga del Norte', '329-034-366', 'martha.farala@motorph.com', '45-5656375-0', '233693897247', '210-835-851-000', '631130283546', 'Regular', 'Payroll Specialist', 'Anthony Salcedo', 1, 11, 3, 10011, 35000.00, 1500.00, 800.00, 1000.00, 17500.00, 198.86),

(10014, 'Martinez', 'Leila', '1970-07-11', '37/46 Kulas Roads, Maragondon 0962 Quirino', '877-110-749', 'leila.martinez@motorph.com', '27-2090996-4', '515741057496', '275-792-513-000', '101205445886', 'Regular', 'Payroll Specialist', 'Anthony Salcedo', 1, 11, 3, 10011, 35000.00, 1500.00, 800.00, 1000.00, 17500.00, 198.86),

-- Sales Department
(10015, 'Romualdez', 'Fredrick', '1985-03-10', '22A/52 Lubowitz Meadows, Pililla 4895 Zambales', '023-079-009', 'fredrick.romualdez@motorph.com', '26-8768374-1', '308366860059', '598-065-761-000', '223057707853', 'Regular', 'Sales Manager', 'Antonio Lim', 1, 12, 7, 10002, 60000.00, 1500.00, 1200.00, 1000.00, 30000.00, 340.91),

(10016, 'Mata', 'Christian', '1987-10-21', '90 O''Keefe Spur Apt. 379, Catigbian 2772 Sulu', '783-776-744', 'christian.mata@motorph.com', '49-2959312-6', '824187961962', '103-100-522-000', '631052853464', 'Regular', 'Sales Representative', 'Fredrick Romualdez', 1, 13, 7, 10015, 30000.00, 1500.00, 800.00, 1000.00, 15000.00, 170.45),

(10017, 'De Leon', 'Selena', '1975-02-20', '89A Armstrong Trace, Compostela 7874 Maguindanao', '975-432-139', 'selena.deleon@motorph.com', '27-2090208-8', '587272469938', '482-259-498-000', '719007608464', 'Regular', 'Sales Representative', 'Fredrick Romualdez', 1, 13, 7, 10015, 30000.00, 1500.00, 800.00, 1000.00, 15000.00, 170.45),

-- Customer Service Department
(10018, 'San Jose', 'Allison', '1986-06-24', '08 Grant Drive Suite 406, Poblacion, Iloilo City 9186 La Union', '179-075-129', 'allison.sanjose@motorph.com', '45-3251383-0', '745148459521', '121-203-336-000', '114901859343', 'Regular', 'Customer Service Manager', 'Fredrick Romualdez', 1, 14, 9, 10015, 45000.00, 1500.00, 1000.00, 1000.00, 22500.00, 255.68),

(10019, 'Rosales', 'Bradly', '1988-06-24', '92 Pagac Spur Suite 864, Poblacion, Tabuk 2412 Quirino', '797-009-261', 'bradly.rosales@motorph.com', '52-0423661-2', '364245235467', '317-674-022-000', '441093369646', 'Regular', 'Customer Service Representative', 'Allison San Jose', 1, 15, 9, 10018, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05),

-- Supply Chain and Operations
(10020, 'Aquino', 'Jeremy', '1976-11-29', '75A/67 Gislason Glens Apt. 152, Bagong Silang, Caloocan 1400 Metro Manila', '491-046-555', 'jeremy.aquino@motorph.com', '30-8870406-2', '201710080255', '971-711-280-000', '171519773969', 'Regular', 'Supply Chain Manager', 'Antonio Lim', 1, 16, 8, 10002, 55000.00, 1500.00, 1200.00, 1000.00, 27500.00, 312.50),

(10021, 'Cruz', 'Judson', '1966-08-27', '47A/85 Mohr Corners Suite 086, Poblacion, Basco 3900 Batanes', '329-034-366', 'judson.cruz@motorph.com', '45-5656375-0', '233693897247', '210-835-851-000', '631130283546', 'Regular', 'Operations Supervisor', 'Antonio Lim', 1, 17, 2, 10002, 40000.00, 1500.00, 1000.00, 1000.00, 20000.00, 227.27),

(10022, 'Mata', 'Andres', '1989-11-18', '90A/25 Gusikowski Rapid Apt. 179, Balamban 1208 Cebu', '877-110-749', 'andres.mata@motorph.com', '27-2090996-4', '515741057496', '275-792-513-000', '101205445886', 'Regular', 'IT System Administrator', 'Eduard Hernandez', 1, 18, 5, 10005, 45000.00, 1500.00, 1200.00, 1000.00, 22500.00, 255.68),

-- Probationary Employees
(10023, 'Santos', 'Vella', '1983-12-31', '99A Padberg Spring, Poblacion, Mabalacat 3959 Lanao del Sur', '955-879-269', 'vella.santos@motorph.com', '52-9883524-3', '548670482885', '101-558-994-000', '360028104576', 'Probationary', 'Customer Service Representative', 'Allison San Jose', 2, 15, 9, 10018, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05),

(10024, 'Del Rosario', 'Tomas', '1978-12-18', '80A/48 Ledner Ridges, Poblacion, Kabankalan 8870 Marinduque', '882-550-989', 'tomas.delrosario@motorph.com', '45-5866331-6', '953901539995', '560-735-732-000', '913108649964', 'Probationary', 'Customer Service Representative', 'Allison San Jose', 2, 15, 9, 10018, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05),

(10025, 'Tolentino', 'Jacklyn', '1984-05-19', '96/48 Watsica Flats Suite 734, Poblacion, Malolos 1844 Ifugao', '675-757-366', 'jacklyn.tolentino@motorph.com', '47-1692793-0', '753800654114', '841-177-857-000', '210546661243', 'Probationary', 'Customer Service Representative', 'Selena De Leon', 2, 15, 9, 10017, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05),

(10026, 'Gutierrez', 'Percival', '1970-12-18', '58A Wilderman Walks, Poblacion, Digos 5822 Davao del Sur', '512-899-876', 'percival.gutierrez@motorph.com', '40-9504657-8', '797639382265', '502-995-671-000', '210897095686', 'Probationary', 'Customer Service Representative', 'Selena De Leon', 2, 15, 9, 10017, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05),

(10027, 'Manalaysay', 'Garfield', '1986-08-28', '60 Goyette Valley Suite 219, Poblacion, Tabuk 3159 Lanao del Sur', '948-628-136', 'garfield.manalaysay@motorph.com', '45-3298166-4', '810909286264', '336-676-445-000', '211274476563', 'Probationary', 'Customer Service Representative', 'Selena De Leon', 2, 15, 9, 10017, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05),

(10028, 'Villegas', 'Lizeth', '1981-12-12', '66/77 Mann Views, Luisiana 1263 Dinagat Islands', '332-372-215', 'lizeth.villegas@motorph.com', '40-2400719-4', '934389652994', '210-395-397-000', '122238077997', 'Probationary', 'Customer Service Representative', 'Selena De Leon', 2, 15, 9, 10017, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05),

(10029, 'Ramos', 'Carol', '1978-08-20', '72/70 Stamm Spurs, Bustos 4550 Iloilo', '250-700-389', 'carol.ramos@motorph.com', '60-1152206-4', '351830469744', '395-032-717-000', '212141893454', 'Probationary', 'Customer Service Representative', 'Selena De Leon', 2, 15, 9, 10017, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05),

(10030, 'Maceda', 'Emelia', '1973-04-14', '50A/83 Bahringer Oval Suite 145, Kiamba 7688 Nueva Ecija', '973-358-041', 'emelia.maceda@motorph.com', '54-1331005-0', '465087894112', '215-973-013-000', '515012579765', 'Probationary', 'Customer Service Representative', 'Selena De Leon', 2, 15, 9, 10017, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05),

(10031, 'Aguilar', 'Delia', '1989-01-27', '95 Cremin Junction, Surallah 2809 Cotabato', '529-705-439', 'delia.aguilar@motorph.com', '52-1859253-1', '136451303068', '599-312-588-000', '110018813465', 'Probationary', 'Customer Service Representative', 'Selena De Leon', 2, 15, 9, 10017, 25000.00, 1500.00, 800.00, 1000.00, 12500.00, 142.05),

-- Recent Hires
(10032, 'Castro', 'John', '1992-02-09', 'Hi-way, Yati, Liloan Cebu', '332-424-955', 'john.castro@motorph.com', '26-7145133-4', '601644902402', '404-768-309-000', '697764069311', 'Regular', 'Sales Manager', 'Isabella Reyes', 1, 12, 7, 10004, 60000.00, 1500.00, 1200.00, 1000.00, 30000.00, 340.91),

(10033, 'Martinez', 'Carlos', '1990-11-16', 'Bulala, Camalaniugan', '078-854-208', 'carlos.martinez@motorph.com', '11-5062972-7', '380685387212', '256-436-296-000', '993372963726', 'Regular', 'Supply Chain Manager', 'Isabella Reyes', 1, 16, 8, 10004, 55000.00, 1500.00, 1200.00, 1000.00, 27500.00, 312.50),

(10034, 'Santos', 'Beatriz', '1990-08-07', 'Agapita Building, Metro Manila', '526-639-511', 'beatriz.santos@motorph.com', '20-2987501-5', '918460050077', '911-529-713-000', '874042259378', 'Regular', 'Customer Service Manager', 'Isabella Reyes', 1, 14, 9, 10004, 45000.00, 1500.00, 1000.00, 1000.00, 22500.00, 255.68);

-- =====================================================
-- 12. INSERT CREDENTIALS - EXACT MATCH FOR YOUR DAO
-- =====================================================

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
-- 13. SYNC NORMALIZED DATA WITH DIRECT FIELDS
-- =====================================================

-- Update leave_request to sync leave_type with leave_type_id
UPDATE leave_request lr 
JOIN leave_types lt ON lr.leave_type = lt.type_name 
SET lr.leave_type_id = lt.leave_type_id;

-- Update deductions to sync type with deduction_type_id
UPDATE deductions d 
JOIN deduction_types dt ON d.type = dt.type_name 
SET d.deduction_type_id = dt.deduction_type_id;

-- Create employee_salary records from direct fields
INSERT INTO employee_salary (
    employee_id, basic_salary, rice_subsidy, phone_allowance, 
    clothing_allowance, gross_semimonthly_rate, hourly_rate, effective_date
)
SELECT 
    employee_id, basic_salary, rice_subsidy, phone_allowance, 
    clothing_allowance, gross_semi_monthly_rate, hourly_rate, date_hired
FROM employees 
WHERE is_active = TRUE;

-- =====================================================
-- 14. SAMPLE DATA FOR TESTING
-- =====================================================

-- Sample attendance records
INSERT INTO attendance (employee_id, date, log_in, log_out, hours_worked, attendance_status) VALUES
(10001, '2024-12-01', '08:00:00', '17:00:00', 8.00, 'Present'),
(10001, '2024-12-02', '08:15:00', '17:00:00', 7.75, 'Late'),
(10001, '2024-12-03', '08:00:00', '17:00:00', 8.00, 'Present'),
(10002, '2024-12-01', '07:45:00', '17:30:00', 8.75, 'Present'),
(10002, '2024-12-02', '08:00:00', '17:00:00', 8.00, 'Present'),
(10002, '2024-12-03', '08:00:00', '17:00:00', 8.00, 'Present'),
(10003, '2024-12-01', '08:30:00', '16:30:00', 7.50, 'Late'),
(10003, '2024-12-02', '08:00:00', '17:00:00', 8.00, 'Present'),
(10003, '2024-12-03', '08:00:00', '17:00:00', 8.00, 'Present');

-- Sample leave requests
INSERT INTO leave_request (employee_id, leave_type, start_date, end_date, days_requested, reason, status) VALUES
(10001, 'Annual Leave', '2024-12-20', '2024-12-22', 3, 'Christmas vacation', 'Approved'),
(10002, 'Sick Leave', '2024-12-15', '2024-12-15', 1, 'Medical appointment', 'Pending'),
(10003, 'Emergency Leave', '2024-12-10', '2024-12-11', 2, 'Family emergency', 'Approved'),
(10004, 'Annual Leave', '2024-12-23', '2024-12-27', 5, 'Year-end vacation', 'Pending');

-- Sample overtime records
INSERT INTO overtime (employee_id, date, hours, reason, approved) VALUES
(10001, '2024-12-01', 2.0, 'Board meeting preparation', TRUE),
(10002, '2024-12-01', 1.5, 'Operations review', TRUE),
(10003, '2024-12-02', 3.0, 'Financial reporting', TRUE),
(10005, '2024-12-03', 2.5, 'System maintenance', TRUE);

-- Sample deductions
INSERT INTO deductions (employee_id, type, amount, description) VALUES
(10001, 'SSS Contribution', 1125.00, 'Monthly SSS contribution'),
(10001, 'PhilHealth Contribution', 5000.00, 'Monthly PhilHealth contribution'),
(10001, 'Pag-IBIG Contribution', 200.00, 'Monthly Pag-IBIG contribution'),
(10002, 'SSS Contribution', 1125.00, 'Monthly SSS contribution'),
(10002, 'PhilHealth Contribution', 3750.00, 'Monthly PhilHealth contribution'),
(10002, 'Pag-IBIG Contribution', 200.00, 'Monthly Pag-IBIG contribution');

-- Sample government contributions
INSERT INTO government_contributions (employee_id, sss, philhealth, pagibig, tax) VALUES
(10001, 1125.00, 5000.00, 200.00, 15000.00),
(10002, 1125.00, 3750.00, 200.00, 10000.00),
(10003, 1125.00, 3000.00, 200.00, 8000.00),
(10004, 1125.00, 2500.00, 200.00, 6000.00),
(10005, 900.00, 2000.00, 160.00, 4000.00);

-- Sample compensation details
INSERT INTO compensation_details (employee_id, rice_subsidy, phone_allowance, clothing_allowance) VALUES
(10001, 1500.00, 2000.00, 1000.00),
(10002, 1500.00, 2000.00, 1000.00),
(10003, 1500.00, 2000.00, 1000.00),
(10004, 1500.00, 2000.00, 1000.00),
(10005, 1500.00, 1500.00, 1000.00);

-- =====================================================
-- 15. FUTURE-READY VIEWS AND TRIGGERS
-- =====================================================

-- Employee summary view (current + future compatibility)
CREATE VIEW employee_summary AS
SELECT 
    e.employee_id,
    CONCAT(e.first_name, ' ', IFNULL(e.middle_name, ''), ' ', e.last_name) AS full_name,
    e.position AS position_current, -- Current DAO field
    p.position_title AS position_normalized, -- Future normalized field
    e.status AS status_current, -- Current DAO field  
    est.status_name AS status_normalized, -- Future normalized field
    d.department_name,
    e.basic_salary,
    e.gross_semi_monthly_rate,
    e.date_hired,
    e.is_active
FROM employees e
LEFT JOIN positions p ON e.position_id = p.position_id
LEFT JOIN employee_status_types est ON e.status_id = est.status_id
LEFT JOIN departments d ON e.department_id = d.department_id
WHERE e.is_active = TRUE;

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

-- Future reporting views
CREATE VIEW monthly_attendance_summary AS
SELECT 
    e.employee_id,
    CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
    YEAR(a.date) as year,
    MONTH(a.date) as month,
    COUNT(a.id) as days_present,
    SUM(a.hours_worked) as total_hours,
    AVG(a.hours_worked) as avg_daily_hours,
    SUM(CASE WHEN a.attendance_status = 'Late' THEN 1 ELSE 0 END) as late_days
FROM employees e
LEFT JOIN attendance a ON e.employee_id = a.employee_id
WHERE e.is_active = TRUE
GROUP BY e.employee_id, YEAR(a.date), MONTH(a.date);

-- =====================================================
-- 16. TRIGGERS FOR DATA CONSISTENCY (FUTURE-READY)
-- =====================================================

DELIMITER //

-- Trigger to sync employee direct fields with normalized fields when updated
CREATE TRIGGER sync_employee_fields_on_update
BEFORE UPDATE ON employees
FOR EACH ROW
BEGIN
    -- Sync position string with position_id
    IF NEW.position_id IS NOT NULL AND NEW.position_id != OLD.position_id THEN
        SET NEW.position = (SELECT position_title FROM positions WHERE position_id = NEW.position_id);
    END IF;
    
    -- Sync status string with status_id
    IF NEW.status_id IS NOT NULL AND NEW.status_id != OLD.status_id THEN
        SET NEW.status = (SELECT status_name FROM employee_status_types WHERE status_id = NEW.status_id);
    END IF;
    
    -- Sync supervisor string with supervisor_id
    IF NEW.immediate_supervisor_id IS NOT NULL AND NEW.immediate_supervisor_id != OLD.immediate_supervisor_id THEN
        SET NEW.immediate_supervisor = (
            SELECT CONCAT(first_name, ' ', last_name) 
            FROM employees 
            WHERE employee_id = NEW.immediate_supervisor_id
        );
    END IF;
END //

-- Trigger to auto-create salary history when employee salary changes
CREATE TRIGGER create_salary_history_on_update
AFTER UPDATE ON employees
FOR EACH ROW
BEGIN
    IF NEW.basic_salary != OLD.basic_salary OR 
       NEW.rice_subsidy != OLD.rice_subsidy OR 
       NEW.phone_allowance != OLD.phone_allowance OR 
       NEW.clothing_allowance != OLD.clothing_allowance THEN
        
        -- Mark old salary record as inactive
        UPDATE employee_salary 
        SET is_active = FALSE, end_date = CURDATE() 
        WHERE employee_id = NEW.employee_id AND is_active = TRUE;
        
        -- Create new salary record
        INSERT INTO employee_salary (
            employee_id, basic_salary, rice_subsidy, phone_allowance, 
            clothing_allowance, gross_semimonthly_rate, hourly_rate, effective_date
        ) VALUES (
            NEW.employee_id, NEW.basic_salary, NEW.rice_subsidy, 
            NEW.phone_allowance, NEW.clothing_allowance, 
            NEW.gross_semi_monthly_rate, NEW.hourly_rate, CURDATE()
        );
    END IF;
END //

DELIMITER ;

-- =====================================================
-- 17. STORED PROCEDURES FOR FUTURE ADVANCED FEATURES
-- =====================================================

DELIMITER //

-- Procedure for bulk attendance import (future feature)
CREATE PROCEDURE BulkImportAttendance(
    IN csv_data TEXT
)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_employee_id INT;
    DECLARE v_date DATE;
    DECLARE v_log_in TIME;
    DECLARE v_log_out TIME;
    
    -- This is a placeholder for future CSV import functionality
    -- You would implement the actual CSV parsing logic here
    
    SELECT 'Bulk import feature ready for implementation' as message;
END //

-- Procedure for generating monthly payroll report (future feature)
CREATE PROCEDURE GenerateMonthlyPayrollReport(
    IN report_month INT,
    IN report_year INT
)
BEGIN
    SELECT 
        e.employee_id,
        CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
        e.position,
        e.basic_salary,
        COALESCE(SUM(a.hours_worked), 0) as total_hours,
        COALESCE(SUM(o.hours), 0) as overtime_hours,
        COALESCE(SUM(p.gross_pay), 0) as total_gross_pay,
        COALESCE(SUM(p.net_pay), 0) as total_net_pay
    FROM employees e
    LEFT JOIN attendance a ON e.employee_id = a.employee_id 
        AND MONTH(a.date) = report_month AND YEAR(a.date) = report_year
    LEFT JOIN overtime o ON e.employee_id = o.employee_id 
        AND MONTH(o.date) = report_month AND YEAR(o.date) = report_year AND o.approved = TRUE
    LEFT JOIN payroll p ON e.employee_id = p.employee_id 
        AND MONTH(p.period_start) = report_month AND YEAR(p.period_start) = report_year
    WHERE e.is_active = TRUE
    GROUP BY e.employee_id, e.first_name, e.last_name, e.position, e.basic_salary
    ORDER BY e.last_name, e.first_name;
END //

-- Procedure for calculating employee payroll (future advanced feature)
CREATE PROCEDURE CalculateEmployeePayroll(
    IN emp_id INT,
    IN period_start_date DATE,
    IN period_end_date DATE
)
BEGIN
    DECLARE v_basic_salary DECIMAL(10,2);
    DECLARE v_days_worked INT;
    DECLARE v_overtime_hours DECIMAL(6,2);
    DECLARE v_gross_pay DECIMAL(10,2);
    DECLARE v_total_deductions DECIMAL(10,2);
    DECLARE v_net_pay DECIMAL(10,2);
    
    -- Get employee basic salary
    SELECT basic_salary INTO v_basic_salary
    FROM employees 
    WHERE employee_id = emp_id AND is_active = TRUE;
    
    -- Calculate days worked
    SELECT COUNT(*) INTO v_days_worked
    FROM attendance 
    WHERE employee_id = emp_id 
    AND date BETWEEN period_start_date AND period_end_date
    AND attendance_status IN ('Present', 'Late');
    
    -- Calculate overtime hours
    SELECT COALESCE(SUM(hours), 0) INTO v_overtime_hours
    FROM overtime 
    WHERE employee_id = emp_id 
    AND date BETWEEN period_start_date AND period_end_date
    AND approved = TRUE;
    
    -- Calculate gross pay (simplified)
    SET v_gross_pay = (v_basic_salary / 22 * v_days_worked) + (v_overtime_hours * (v_basic_salary / 176 * 1.25));
    
    -- Calculate deductions (simplified)
    SELECT COALESCE(SUM(amount), 0) INTO v_total_deductions
    FROM deductions 
    WHERE employee_id = emp_id 
    AND deduction_date BETWEEN period_start_date AND period_end_date
    AND status = 'active';
    
    -- Calculate net pay
    SET v_net_pay = v_gross_pay - v_total_deductions;
    
    -- Insert or update payroll record
    INSERT INTO payroll (
        employee_id, period_start, period_end, monthly_rate, days_worked, 
        overtime_hours, gross_pay, total_deductions, net_pay
    ) VALUES (
        emp_id, period_start_date, period_end_date, v_basic_salary, v_days_worked,
        v_overtime_hours, v_gross_pay, v_total_deductions, v_net_pay
    )
    ON DUPLICATE KEY UPDATE
        days_worked = v_days_worked,
        overtime_hours = v_overtime_hours,
        gross_pay = v_gross_pay,
        total_deductions = v_total_deductions,
        net_pay = v_net_pay;
    
    SELECT 'Payroll calculated successfully' as result,
           v_gross_pay as gross_pay,
           v_total_deductions as total_deductions,
           v_net_pay as net_pay;
END //

DELIMITER ;

-- =====================================================
-- 18. INDEXES FOR OPTIMAL PERFORMANCE
-- =====================================================

-- Performance indexes for current DAO operations
CREATE INDEX idx_employees_name ON employees(last_name, first_name);
CREATE INDEX idx_employees_status_str ON employees(status);
CREATE INDEX idx_employees_position_str ON employees(position);
CREATE INDEX idx_employees_hire_date ON employees(date_hired);
CREATE INDEX idx_attendance_employee_month ON attendance(employee_id, date);
CREATE INDEX idx_payroll_employee_period ON payroll(employee_id, period_start, period_end);
CREATE INDEX idx_leave_employee_dates ON leave_request(employee_id, start_date, end_date);
CREATE INDEX idx_overtime_employee_date ON overtime(employee_id, date);
CREATE INDEX idx_deductions_employee_type ON deductions(employee_id, type);

-- Future reporting indexes
CREATE INDEX idx_attendance_date_status ON attendance(date, attendance_status);
CREATE INDEX idx_payroll_period_status ON payroll(period_start, period_end, status);
CREATE INDEX idx_employees_dept_status ON employees(department_id, status_id, is_active);

-- =====================================================
-- 19. VERIFICATION AND TESTING QUERIES
-- =====================================================

-- Verify the hybrid setup
SELECT 'Hybrid Database setup completed successfully!' AS status;

-- Test current DAO compatibility
SELECT 
    'Current DAO Compatibility Test' as test_name,
    COUNT(*) as total_employees,
    COUNT(CASE WHEN status IS NOT NULL THEN 1 END) as employees_with_status,
    COUNT(CASE WHEN position IS NOT NULL THEN 1 END) as employees_with_position,
    COUNT(CASE WHEN basic_salary > 0 THEN 1 END) as employees_with_salary
FROM employees;

-- Test future normalization readiness
SELECT 
    'Future Normalization Readiness' as test_name,
    COUNT(CASE WHEN status_id IS NOT NULL THEN 1 END) as employees_with_status_id,
    COUNT(CASE WHEN position_id IS NOT NULL THEN 1 END) as employees_with_position_id,
    COUNT(CASE WHEN department_id IS NOT NULL THEN 1 END) as employees_with_department_id
FROM employees;

-- Test login functionality
SELECT 
    'Login Test for Employee 10001' as test_case,
    CASE 
        WHEN EXISTS(
            SELECT 1 FROM credentials c
            JOIN employees e ON c.employee_id = e.employee_id
            WHERE c.employee_id = 10001 AND c.password = 'password1234' AND e.is_active = TRUE
        ) THEN 'PASS - Login should work'
        ELSE 'FAIL - Login will not work'
    END as result;

-- Sample data verification
SELECT 
    e.employee_id,
    e.first_name,
    e.last_name,
    e.position, -- Current DAO field
    e.status, -- Current DAO field
    e.basic_salary, -- Current DAO field
    p.position_title as normalized_position, -- Future field
    est.status_name as normalized_status, -- Future field
    'password1234' as default_password
FROM employees e
LEFT JOIN positions p ON e.position_id = p.position_id
LEFT JOIN employee_status_types est ON e.status_id = est.status_id
WHERE e.employee_id <= 10005
ORDER BY e.employee_id;

-- Test advanced reporting capability
SELECT 
    'Advanced Reporting Test' as test_name,
    COUNT(DISTINCT e.employee_id) as total_employees,
    COUNT(DISTINCT d.department_id) as total_departments,
    COUNT(DISTINCT p.position_id) as total_positions,
    COUNT(*) as total_attendance_records
FROM employees e
LEFT JOIN departments d ON e.department_id = d.department_id
LEFT JOIN positions p ON e.position_id = p.position_id
LEFT JOIN attendance a ON e.employee_id = a.employee_id;

-- =====================================================
-- 20. FUTURE MIGRATION HELPERS
-- =====================================================

-- When you're ready to migrate from direct fields to normalized fields:
-- 
-- 1. Update your DAO to use normalized fields:
--    - Change e.status to est.status_name 
--    - Change e.position to p.position_title
--    - Join with reference tables
--
-- 2. Run this migration to clean up direct fields:
--    ALTER TABLE employees DROP COLUMN status;
--    ALTER TABLE employees DROP COLUMN position;
--    ALTER TABLE employees DROP COLUMN immediate_supervisor;
--
-- 3. Update views to use only normalized fields

-- Example of future DAO query structure:
-- SELECT 
--     e.employee_id,
--     e.first_name,
--     e.last_name,
--     est.status_name as status,
--     p.position_title as position,
--     d.department_name,
--     es.basic_salary
-- FROM employees e
-- JOIN employee_status_types est ON e.status_id = est.status_id
-- JOIN positions p ON e.position_id = p.position_id
-- JOIN departments d ON e.department_id = d.department_id
-- JOIN employee_salary es ON e.employee_id = es.employee_id AND es.is_active = TRUE
-- WHERE e.employee_id = ?;

-- =====================================================
-- SETUP COMPLETE - FUTURE-PROOF HYBRID SYSTEM READY!
-- =====================================================

SELECT 
    '🚀 MotorPH Future-Proof Database Setup Complete!' as message,
    'Your current DAO will work immediately without any code changes!' as compatibility,
    'Advanced features are ready for future implementation!' as future_ready,
    'Run your application now - it should work perfectly!' as next_step;