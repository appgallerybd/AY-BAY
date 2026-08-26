package com.example.core.localization

object AppStrings {
    fun get(key: String, lang: Language): String {
        val entry = strings[key] ?: return key
        return if (lang == Language.BANGLA) entry.bn else entry.en
    }

    private data class Str(val en: String, val bn: String)

    private val strings = mapOf(
        // Navigation & Titles
        "app_name" to Str("FinFlow", "ফিনফ্লো"),
        "nav_home" to Str("Dashboard", "ড্যাশবোর্ড"),
        "nav_transactions" to Str("Transactions", "লেনদেন"),
        "nav_budgets" to Str("Budgets", "বাজেট"),
        "nav_reports" to Str("Analytics", "বিশ্লেষণ"),
        "nav_more" to Str("More", "আরও"),

        // Financial Overview
        "total_balance" to Str("Total Balance", "মোট ব্যালেন্স"),
        "total_income" to Str("Total Income", "মোট আয়"),
        "total_expense" to Str("Total Expense", "মোট ব্যয়"),
        "net_cash_flow" to Str("Net Balance", "নেট সঞ্চয়"),
        "income" to Str("Income", "আয়"),
        "expense" to Str("Expense", "ব্যয়"),
        "transfer" to Str("Transfer", "ট্রান্সফার"),
        "debt" to Str("Debt & Loan", "দেনা-পাওনা"),
        "savings" to Str("Savings Goal", "সঞ্চয় লক্ষ্য"),
        "budget" to Str("Budget", "বাজেট"),
        "subscriptions" to Str("Recurring", "সাবস্ক্রিপশন"),
        "calendar" to Str("Calendar", "ক্যালেন্ডার"),
        "accounts" to Str("Accounts & Wallets", "অ্যাকাউন্ট ও ওয়ালেট"),
        "categories" to Str("Categories", "ক্যাটাগরি"),

        // Quick Actions & Time Filters
        "quick_actions" to Str("Quick Actions", "দ্রুত অ্যাকশন"),
        "add_income" to Str("Add Income", "আয় যোগ"),
        "add_expense" to Str("Add Expense", "ব্যয় যোগ"),
        "add_transfer" to Str("New Transfer", "ট্রান্সফার"),
        "today" to Str("Today", "আজ"),
        "this_week" to Str("This Week", "এই সপ্তাহ"),
        "this_month" to Str("This Month", "এই মাস"),
        "this_year" to Str("This Year", "এই বছর"),
        "all_time" to Str("All Time", "সব সময়"),
        "custom" to Str("Custom", "কাস্টম"),

        // Section Headers
        "recent_transactions" to Str("Recent Transactions", "সাম্প্রতিক লেনদেন"),
        "view_all" to Str("View All", "সব দেখুন"),
        "expense_by_category" to Str("Expense by Category", "ক্যাটাগরি ভিত্তিক খরচ"),
        "budget_progress" to Str("Budget Health", "বাজেটের অবস্থা"),
        "upcoming_bills" to Str("Upcoming Subscriptions", "আসন্ন সাবস্ক্রিপশন ও বিল"),
        "outstanding_debts" to Str("Outstanding Debts", "বকেয়া দেনা-পাওনা"),
        "financial_insights" to Str("Smart Financial Insights", "স্মার্ট আর্থিক অন্তর্দৃষ্টি"),

        // Transaction Details
        "amount" to Str("Amount", "টাকার পরিমাণ"),
        "category" to Str("Category", "ক্যাটাগরি"),
        "account" to Str("Account / Wallet", "অ্যাকাউন্ট / ওয়ালেট"),
        "from_account" to Str("From Account", "উৎস অ্যাকাউন্ট"),
        "to_account" to Str("To Account", "গন্তব্য অ্যাকাউন্ট"),
        "date" to Str("Date", "তারিখ"),
        "time" to Str("Time", "সময়"),
        "note" to Str("Note / Description", "বিবরণ / নোট"),
        "payment_method" to Str("Payment Method", "পেমেন্ট মাধ্যম"),
        "tags" to Str("Tags", "ট্যাগ"),
        "person" to Str("Person / Contact", "ব্যক্তি / প্রতিষ্ঠান"),
        "save_transaction" to Str("Save Transaction", "লেনদেন সংরক্ষণ করুন"),
        "edit_transaction" to Str("Edit Transaction", "লেনদেন সম্পাদনা"),
        "delete_transaction" to Str("Delete Transaction", "মুছে ফেলুন"),
        "delete_confirm" to Str("Are you sure you want to delete this?", "আপনি কি নিশ্চিত এটি মুছে ফেলতে চান?"),
        "cancel" to Str("Cancel", "বাতিল"),
        "delete" to Str("Delete", "মুছে ফেলুন"),
        "save" to Str("Save", "সংরক্ষণ"),
        "add" to Str("Add", "যোগ করুন"),
        "create" to Str("Create", "তৈরি করুন"),

        // Smart Presets
        "preset_lunch" to Str("Lunch ৳250", "দুপুরের খাবার ২৫০৳"),
        "preset_tea" to Str("Tea & Snacks ৳60", "চা-নাস্তা ৬০৳"),
        "preset_grocery" to Str("Grocery ৳1500", "বাজার ১৫০০৳"),
        "preset_rickshaw" to Str("Transport ৳80", "রিকশা/গাড়ি ৮০৳"),
        "preset_salary" to Str("Monthly Salary", "মাসিক বেতন"),

        // Budgets & Savings
        "create_budget" to Str("Create Budget", "নতুন বাজেট"),
        "budget_limit" to Str("Budget Limit", "বাজেট সীমা"),
        "spent" to Str("Spent", "খরচ হয়েছে"),
        "remaining" to Str("Remaining", "অবশিষ্ট"),
        "overbudget" to Str("Over Budget!", "বাজেট অতিক্রম করেছে!"),
        "near_limit" to Str("Near Limit (80%+)", "সীমার কাছাকাছি (৮০%+)"),
        "safe_budget" to Str("On Track", "নিয়ন্ত্রণে আছে"),
        "savings_goals" to Str("Savings Goals", "সঞ্চয়ের লক্ষ্য"),
        "create_goal" to Str("Create Savings Goal", "নতুন সঞ্চয় লক্ষ্য"),
        "target_amount" to Str("Target Amount", "লক্ষ্যমাত্রা"),
        "current_saved" to Str("Saved", "জমা হয়েছে"),
        "target_date" to Str("Deadline", "মেয়াদ"),
        "add_contribution" to Str("Add Savings", "টাকা জমা করুন"),
        "monthly_needed" to Str("Monthly saving needed", "প্রতি মাসে সঞ্চয় দরকার"),

        // Debts
        "i_owe" to Str("I Owe (Payable)", "আমি দেনাদার (দিতে হবে)"),
        "owes_me" to Str("Owes Me (Receivable)", "পাওনাদার (পাবো)"),
        "total_payable" to Str("Total Payable", "মোট দেনা"),
        "total_receivable" to Str("Total Receivable", "মোট পাওনা"),
        "add_debt" to Str("Add Debt / Loan", "দেনা-পাওনা যোগ করুন"),
        "record_payment" to Str("Record Payment", "পেমেন্ট গ্রহণ/প্রদান"),
        "mark_settled" to Str("Settle Debt", "সম্পূর্ণ পরিশোধ করুন"),
        "settled" to Str("Settled", "পরিশোধিত"),

        // Subscriptions
        "add_recurring" to Str("Add Subscription", "সাবস্ক্রিপশন যোগ করুন"),
        "frequency" to Str("Frequency", "পুনরাবৃত্তি"),
        "frequency_daily" to Str("Daily", "দৈনিক"),
        "frequency_weekly" to Str("Weekly", "সাপ্তাহিক"),
        "frequency_monthly" to Str("Monthly", "মাসিক"),
        "frequency_yearly" to Str("Yearly", "বার্ষিক"),
        "next_due" to Str("Next Due", "পরবর্তী তারিখ"),

        // Reports & Analytics
        "income_vs_expense" to Str("Income vs Expense", "আয় বনাম ব্যয়"),
        "category_breakdown" to Str("Category Breakdown", "ক্যাটাগরি ভিত্তিক অনুপাত"),
        "cashflow_summary" to Str("Cashflow Summary", "ক্যাশফ্লো বিবরণ"),
        "top_expenses" to Str("Top Expenses", "শীর্ষ খরচ"),
        "export_csv" to Str("Export to CSV", "CSV এক্সপোর্ট"),
        "share_summary" to Str("Share Financial Report", "আর্থিক রিপোর্ট শেয়ার"),

        // More & Settings
        "settings" to Str("Settings & Preferences", "সেটিংস ও পছন্দসমূহ"),
        "app_lock" to Str("Security & App PIN Lock", "নিরাপত্তা ও পিন লক"),
        "security_pin" to Str("4-Digit PIN Lock", "৪-সংখ্যার পিন লক"),
        "enter_pin" to Str("Enter 4-Digit Security PIN", "৪-সংখ্যার নিরাপত্তা পিন দিন"),
        "create_pin" to Str("Set New Security PIN", "নতুন পিন কোড সেট করুন"),
        "confirm_pin" to Str("Confirm PIN", "পিন নিশ্চিত করুন"),
        "pin_incorrect" to Str("Incorrect PIN, try again", "ভুল পিন, আবার চেষ্টা করুন"),
        "pin_enabled_msg" to Str("App lock enabled securely", "অ্যাপ লক সক্রিয় করা হয়েছে"),
        "data_management" to Str("Data & Backup", "ডাটা ও ব্যাকআপ"),
        "backup_json" to Str("Backup Data (JSON)", "সম্পূর্ণ ব্যাকআপ (JSON)"),
        "restore_json" to Str("Restore Data (JSON)", "রিস্টোর ডাটা (JSON)"),
        "clear_data" to Str("Reset / Erase All Data", "সব ডাটা মুছে নতুন করে শুরু"),
        "load_sample" to Str("Load Demo/Sample Data", "নমুনা ডাটা লোড করুন"),
        "language_choice" to Str("App Language", "অ্যাপের ভাষা"),
        "currency_choice" to Str("Primary Currency", "প্রধান মুদ্রা"),
        "theme_choice" to Str("Appearance / Theme", "থিম ও ডিসপ্লে"),
        "theme_light" to Str("Light Theme", "লাইট থিম"),
        "theme_dark" to Str("Dark Theme", "ডার্ক থিম"),
        "theme_system" to Str("System Default", "সিস্টেম ডিফল্ট"),
        "about_finflow" to Str("About FinFlow", "ফিনফ্লো সম্পর্কে"),
        "privacy_policy" to Str("Privacy Policy & Security", "গোপনীয়তা নীতি ও নিরাপত্তা"),
        "offline_first_notice" to Str("100% Offline & Private on your device", "সম্পূর্ণ অফলাইন এবং ডিভাইসে নিরাপদ"),
        "version" to Str("Version 1.0.0 (Production)", "সংস্করণ ১.০.০ (প্রোডাকশন)"),

        // Search & Empty states
        "search_hint" to Str("Search by note, category, person, tag...", "নোট, ক্যাটাগরি, ব্যক্তি বা ট্যাগ দিয়ে খুঁজুন..."),
        "no_transactions_title" to Str("No transactions recorded yet", "এখনও কোনো লেনদেন নেই"),
        "no_transactions_sub" to Str("Add your first income or expense to start tracking your wealth.", "আপনার আয় বা ব্যয় যোগ করে ট্র্যাকিং শুরু করুন।"),
        "no_budgets" to Str("No active budgets. Create one to keep expenses controlled.", "কোনো বাজেট তৈরি করা নেই। ব্যয় নিয়ন্ত্রণে বাজেট যোগ করুন।"),
        "no_goals" to Str("No savings goals yet. Set a financial milestone today!", "কোনো সঞ্চয় লক্ষ্য নেই। আজই নতুন লক্ষ্য নির্ধারণ করুন!"),
        "no_debts" to Str("No outstanding debts or loans recorded.", "কোনো দেনা বা পাওনা রেকর্ড নেই।"),
        "no_recurring" to Str("No active subscriptions or recurring bills.", "কোনো সাবস্ক্রিপশন বা নিয়মিত বিল নেই।"),
        "undo" to Str("Undo", "পুনরুদ্ধার"),
        "deleted_success" to Str("Transaction deleted", "লেনদেন মুছে ফেলা হয়েছে"),
        "created_success" to Str("Saved successfully", "সফলভাবে সংরক্ষিত হয়েছে"),
        "welcome_title" to Str("Welcome to FinFlow", "ফিনফ্লো-তে স্বাগতম"),
        "welcome_sub" to Str("Your intelligent, private, and premium personal finance manager.", "আপনার বিশ্বমানের, নিরাপদ ও পূর্ণাঙ্গ ব্যক্তিগত অর্থ ব্যবস্থাপনা সিস্টেম।"),
        "get_started" to Str("Get Started", "শুরু করুন"),
        "skip" to Str("Skip", "এড়িয়ে যান"),
        "next" to Str("Next", "পরবর্তী"),
        "finish" to Str("Start Tracking", "ট্র্যাকিং শুরু করুন")
    )
}
