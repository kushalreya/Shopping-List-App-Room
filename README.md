# 🛒 NearCart

## 📱 App Overview
**NearCart** is a smart shopping list Android app that helps users manage their grocery items while also discovering nearby stores in real time.

It solves two common problems:
- Forgetting what to buy while shopping  
- Not knowing the nearest store to quickly get items  

By combining a **shopping list manager + location-based store finder**, NearCart makes shopping faster and more efficient.

---

## ✨ Features

- 📝 Add, update, and delete shopping items
- 📦 Quantity & unit management (kgs, lts, pieces, etc.)
- 📍 Real-time location tracking
- 🗺 Interactive Google Maps location selector
- 🏪 Nearby grocery/store discovery using Places API
- 📏 Smart nearest store detection based on distance
- 🧭 One-tap navigation to nearest store
- 🎨 Light / Dark / System theme support
- 🔄 Swipe-to-delete with confirmation
- 📡 Pagination support for nearby places API
- 📶 Works offline for saved shopping list (Room DB)

  ### 💡 Smart UX Highlight

- ✅ Automatically sorts completed (checked) items to the bottom  
  → Keeps your active shopping items always on top  

---

## 🛠 Tech Stack

**Language**
- Kotlin

**UI**
- Jetpack Compose (Material 3)

**Architecture**
- MVVM (Model-View-ViewModel)

**Database**
- Room Database (Offline storage)

**Networking**
- Retrofit
- Gson Converter

**Location & Maps**
- Google Maps Compose
- Fused Location Provider
- Google Places API
- Google Geocoding API

**Other**
- Coroutines (async operations)
- Flow (reactive data streams)

---

## 🏗 Architecture

The app follows a clean **MVVM architecture** ensuring separation of concerns and scalability.

### Data Flow:
```mermaid
graph TD
    UI[Compose UI] --> VM[ViewModel]
    VM --> Repo[Repository]
    Repo --> DB[Room Database]
    Repo --> API[Google APIs]
    DB --> Repo
    API --> Repo
    Repo --> VM
    VM --> UI
```

---

## 🏗 Layers

- **UI Layer** → Composables (Screens)  
- **ViewModel Layer** → Business logic & state  
- **Repository Layer** → Single source of truth  
- **Data Layer** → Room DB + API  

---

## 🔄 App Flow

1. App launches → asks for location permission  
2. User lands on **Home Screen**  
3. If no location is set → navigates to **Location Selector**  
4. User selects location on map  
5. App:
   - Fetches address (Geocoding API)  
   - Fetches nearby stores (Places API)  
6. User:
   - Adds shopping items  
   - Updates or deletes items  
7. App shows:
   - Shopping list  
   - Nearest store with distance  
8. User can:
   - Navigate to store via Google Maps  
   - Change location anytime

---

## 🎥 Demo Video

> Demo video link here

```md
https://demo-video-link.com
```

---

## 🌐 API Integration

### APIs Used

- **Google Geocoding API**
  - Converts latitude/longitude → readable address  

- **Google Places API**
  - Fetches nearby grocery stores, supermarkets, malls  

---

### How Data is Fetched

- Retrofit is used to call APIs  
- Responses are parsed using Gson  
- Pagination handled using `next_page_token`  
- Data is merged and deduplicated  

---

### Error Handling

- Try-catch blocks in ViewModel  
- Logs for debugging (`Log.d`)  
- Safe UI fallback (empty states)  

---

## 📂 Project Structure
```
kush.android.nearcart
│
├── model/              # Data models & Room DB
├── network_call/       # Retrofit API services
├── navigation/         # Navigation routes
├── ui/theme/           # Themes & UI styling
├── util/               # Utilities (location, permissions, helpers)
├── view/               # Composable screens
├── viewmodel/          # ViewModels (business logic)
│
├── Graph.kt            # Dependency provider
├── MainActivity.kt     # Entry point
└── ShoppingListApp.kt  # Application class
```

---

## 🎯 Use Cases

- 🛒 Grocery shopping planning  
- 🏃 Quick nearby store discovery  
- 📋 Daily household item tracking  
- 🧠 Reducing memory load (no more forgetting items)  
- 🗺 Finding closest store during travel  

---

## 🚧 Future Improvements

- 🔐 User authentication (Firebase/Auth)  
- ☁️ Cloud sync for shopping list  
- 🧠 Smart recommendations (AI-based suggestions)  
- 📊 Analytics for shopping habits  
- 🛍 Store categories & filters  
- 🔔 Reminder notifications  
- 📷 Barcode scanner for items  

---

## 💼 Portfolio & Freelancing

This project is part of my Android development portfolio showcasing:

- Modern Android development (Compose + MVVM)  
- API integration  
- Real-world problem solving  

🚀 I’m open to freelancing, internships, and collaboration opportunities.  

Feel free to reach out!

---

## ⭐ Support

If you found this project helpful:

- ⭐ Star the repo  
- 🍴 Fork it  
- 🧠 Share feedback

---
