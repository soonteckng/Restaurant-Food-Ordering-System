# LeanBites - Restaurant Food Ordering System

A feature-rich restaurant ordering system built with Scala, ScalaFX, and Apache Derby database. LeanBites provides a complete food ordering experience with user authentication, cart management, calorie tracking, and order history.

## Demo Video

[![LeanBites Demo](https://img.youtube.com/vi/b9YZ4JzZ6Gg/maxresdefault.jpg)](https://www.youtube.com/watch?v=b9YZ4JzZ6Gg)

*Click the image above to watch the demo video*

## Features

### User Management
- **User Registration & Login** – Secure authentication system with password validation
- **Session Management** – Persistent user sessions throughout the application
- **Order History Tracking** – View past orders with detailed information

### Food Catalog
- **60+ Food Items** – Diverse menu across 10+ cuisines (Italian, Japanese, Chinese, Mexican, Indian, Thai, American, Mediterranean, Fast Food, Healthy)
- **Advanced Filtering** – Search by name, category, or health rating
- **Detailed Food Information** – View calories, ingredients, serving size, and health ratings
- **Double-Click Details View** – Quick access to comprehensive food information

### Smart Cart System
- **Real-time Cart Updates** – Live cart summary with item count and total price
- **Quantity Management** – Add, update, or remove items with validation
- **Cart Limits** – Maximum 10 items per food type, 50 items total
- **Health Tracking** – Automatic calorie calculation and health status indicators

### Payment Processing
- **Multiple Payment Methods** – Credit Card, Debit Card, and E-Wallet support
- **Card Validation** – Comprehensive validation for card number, expiry date, CVV, and cardholder name
- **Order Confirmation** – Unique order number generation and receipt display
- **Tax Calculation** – Automatic 6% tax computation

### Analytics & Tracking
- **Calorie Summary** – View total and average calories per item
- **Health Status Indicators** – Color-coded health ratings (Low, Moderate, High, Very High Calorie)
- **Order History** – Complete transaction history with dates and totals
- **Cart Summary** – Real-time subtotal, tax, and final total calculations

### User Interface
- **Modern Design** – Custom CSS theming with food-centric aesthetics
- **Responsive Tables** – Interactive TableViews with sortable columns
- **Modal Dialogs** – Clean checkout, food details, and calorie summary dialogs
- **Navigation Tabs** – Easy switching between Home, Cart, and Order History views

## Technical Stack

### Core Technologies
- **Language**: Scala 3
- **UI Framework**: ScalaFX (JavaFX wrapper)
- **Database**: Apache Derby (Embedded)
- **ORM**: ScalikeJDBC


## Getting Started

### Prerequisites

- **Java Development Kit (JDK)** 11 or higher
- **Scala** 3.x
- **SBT** (Scala Build Tool) 1.x
- **JavaFX** 17 or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/soonteckng/LeanBites---A-Restaurant-Ordering-System.git
   cd LeanBites---A-Restaurant-Ordering-System
   ```

2. **Build the project**
   ```bash
   sbt compile
   ```

3. **Run the application**
   ```bash
   sbt run
   ```

4. **Database initialization**
   - The database will be automatically created on first run
   - Default food items (60+ entries) will be loaded automatically
   - Database location: `./myDB/` (created in project root)

## Usage Guide

### First Time Setup

1. **Register an Account**
   - Click "Register" on the login screen
   - Username must be at least 3 characters
   - Password must be at least 6 characters
   - Confirm password must match

2. **Login**
   - Enter your username and password
   - Click "Login" to access the main application

### Ordering Food

1. **Browse Menu**
   - View all available food items in the table
   - Use the search bar to find specific items
   - Filter by category using the dropdown
   - Click "Healthy Options" to show only low-calorie items

2. **View Food Details**
   - Double-click any food item to view detailed information
   - See ingredients, serving size, and nutritional information
   - Quick add to cart directly from details view

3. **Add to Cart**
   - Select a food item from the table
   - Choose quantity using the spinner (1-10)
   - Click "Add to Cart"
   - View real-time cart updates in the badge

### Managing Your Cart

1. **View Cart**
   - Click "Cart" navigation button
   - See all items with quantities, prices, and calories
   - View cart summary with totals and health status

2. **Modify Cart**
   - Remove individual items by selecting and clicking "Remove"
   - Clear entire cart with "Clear Cart" button
   - View calorie breakdown with "Calculate Calories"

3. **Checkout**
   - Click "Checkout" button
   - Review order summary
   - Select payment method (E-Wallet, Credit/Debit Card)
   - Fill in payment details (if card payment)
   - Click "Process Payment"
   - Receive order confirmation

### Payment Information

#### E-Wallet
- No additional information required
- Instant processing

#### Credit/Debit Card
- **Card Number**: 16 digits (e.g., 1234567890123456)
- **Cardholder Name**: Letters and spaces only
- **Expiry Date**: MM/YY format (must be future date, max 10 years)
- **CVV**: 3 digits

### Order History

- Click "Order History" navigation button
- View all past orders with:
  - Order date and time
  - Items ordered
  - Total amount
  - Order status

## Database Schema

### Users Table
```sql
CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) UNIQUE NOT NULL,
  password VARCHAR(64) NOT NULL
)
```

### FoodType Table
```sql
CREATE TABLE foodtype (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(200) NOT NULL,
  category VARCHAR(100) NOT NULL,
  price DOUBLE NOT NULL,
  calories INT NOT NULL,
  description VARCHAR(500) NOT NULL
)
```

### Order_History Table
```sql
CREATE TABLE order_history (
  id INT PRIMARY KEY AUTO_INCREMENT,
  order_id VARCHAR(50) NOT NULL,
  order_date VARCHAR(50) NOT NULL,
  items VARCHAR(500),
  total DOUBLE,
  status VARCHAR(20),
  username VARCHAR(64) NOT NULL,
  FOREIGN KEY (username) REFERENCES users(username)
)
```

## 🎯 Key Features Explained

### Cart Validation
- **Item Limits**: Maximum 10 units per food item
- **Total Limit**: Maximum 50 items across all food types
- **Duplicate Prevention**: Automatically updates quantity for existing items
- **Price Calculation**: Real-time price updates with tax (6%)

### Health Tracking
The system categorizes food based on calorie content.


## Configuration

### Database Configuration
Located in `Database.scala`:
```scala
val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"
val dbURL = "jdbc:derby:myDB;create=true"
```

### Tax Rate
Located in `Cart.scala`:
```scala
def getTax: Double = getTotalPrice * 0.06  // 6% tax
```

### Cart Limits
Located in `Cart.scala`:
```scala
private val maxItemsPerFood = 10
private val maxTotalItems = 50
```

## Customization

### Adding New Food Categories
Modify `FoodType.scala`:
```scala
// Add new food items in resetDefaultFoods()
new FoodType("New Dish", "New Category", 25.00, 500, "Description")
```

### Changing Theme
Edit `FoodTheme.css` to customize:
- Colors and backgrounds
- Button styles
- Table appearance
- Dialog styling

### Modifying Validation Rules
Update validation methods in respective model classes:
- `User.scala` - Registration validation
- `Payment.scala` - Payment validation
- `Cart.scala` - Cart limits

## Troubleshooting

### Database Issues
- **Database locked**: Close all running instances of the application
- **Corrupted database**: Delete `myDB/` folder and restart (will reset all data)

### UI Not Displaying
- **JavaFX not found**: Ensure JavaFX is properly installed and configured
- **FXML loading errors**: Check that all FXML files are in correct paths under `resources/soonteck/view/`

### Payment Validation Fails
- **Card number**: Must be exactly 16 digits, no spaces or dashes
- **Expiry date**: Use MM/YY format (e.g., 12/25)
- **CVV**: Must be exactly 3 digits

## Sample Data

The system comes pre-loaded with 60+ food items including:

**Italian** (7 items): Margherita Pizza, Carbonara Pasta, Lasagna, etc.  
**Japanese** (8 items): Salmon Sashimi, Beef Ramen, Gyoza, etc.  
**Chinese** (7 items): Kung Pao Chicken, Peking Duck, Dim Sum, etc.  
**Mexican** (6 items): Chicken Tacos, Beef Burrito, Nachos, etc.  
**Indian** (6 items): Butter Chicken, Lamb Biryani, Samosa, etc.  
**Thai** (5 items): Pad Thai, Green Curry, Tom Yum Soup, etc.  
**Fast Food** (8 items): Cheeseburger, Chicken Wings, Hot Dog, etc.  
**Healthy** (6 items): Quinoa Bowl, Grilled Salmon, Veggie Smoothie Bowl, etc.  
**American** (4 items): BBQ Ribs, Mac and Cheese, Pancakes, etc.  
**Mediterranean** (5 items): Caesar Salad, Lamb Souvlaki, Falafel Wrap, etc.


## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Developer

**Soon Teck Ng**  
GitHub: [@soonteckng](https://github.com/soonteckng)

---

