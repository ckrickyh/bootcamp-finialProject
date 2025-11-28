# Spring Boot Dependency Injection Explained

## Overview

Spring Boot uses **Dependency Injection (DI)** to automatically create and connect objects in your application. The `@Autowired` annotation tells Spring to automatically provide the dependencies you need.

---

## How Spring Boot Starts Your Application

When Spring Boot starts, it follows these steps:

### Step 1: Component Scanning
Spring scans all classes with these annotations:
- `@Service` - Business logic classes
- `@Repository` - Database access classes
- `@Controller` / `@RestController` - HTTP request handlers
- `@Component` - General Spring components

### Step 2: Bean Creation
Spring creates objects (called "beans") for each annotated class and stores them in the **Application Context**.

### Step 3: Dependency Wiring
Spring automatically connects dependencies using `@Autowired` annotations.

---

## Dependency Injection Flow

### Architecture Layers

```
┌─────────────────────────────────────────────────────────────┐
│                    CONTROLLER LAYER                        │
│  (Handles HTTP Requests)                                    │
│                                                              │
│  ProviderController                                          │
│  ├─ @Autowired ProviderService                               │
│  └─ Uses: providerService.getAllFinnhubData()              │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ @Autowired
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                     SERVICE LAYER                           │
│  (Business Logic)                                           │
│                                                              │
│  ProviderServiceImpl                                         │
│  ├─ @Autowired FinnhubRepository                            │
│  ├─ @Autowired HistoryRepository                            │
│  ├─ @Autowired ProfileRepository                            │
│  └─ Uses: finnhubRepository.save()                          │
└─────────────────────────────────────────────────────────────┘
                            │
                            │ @Autowired
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                   REPOSITORY LAYER                          │
│  (Database Access)                                          │
│                                                              │
│  FinnhubRepository                                          │
│  ├─ Extends JpaRepository                                   │
│  └─ Spring creates database connection automatically        │
└─────────────────────────────────────────────────────────────┘
```

---

## Real Example from Your Codebase

### 1. Controller Layer

**File:** `ProviderController.java`

```java
@RestController
public class ProviderController {
    
    @Autowired
    ProviderService providerService;  // ← Spring injects ProviderServiceImpl
    
    public List<ResponseDTO> getFinnhub() {
        // Use the service - Spring already created it!
        return this.providerService.getAllFinnhubData();
    }
}
```

**What happens:**
- Spring finds `ProviderServiceImpl` (has `@Service` annotation)
- Spring creates an instance of `ProviderServiceImpl`
- Spring puts it into the `providerService` variable
- You can use it immediately!

---

### 2. Service Layer

**File:** `ProviderServiceImpl.java`

```java
@Service
public class ProviderServiceImpl implements ProviderService {
    
    @Autowired
    FinnhubRepository finnhubRepository;  // ← Spring injects repository
    
    @Autowired
    HistoryRepository historyRepository;   // ← Spring injects repository
    
    @Autowired
    ProfileRepository profileRepository;    // ← Spring injects repository
    
    public void saveFinnhubData(FinnhubDTO dto) {
        // Use repositories - Spring already created them!
        this.finnhubRepository.save(entity);
    }
}
```

**What happens:**
- Spring finds repository interfaces
- Spring creates implementations automatically (via JPA)
- Spring connects them to the database
- You can use them immediately!

---

### 3. Repository Layer

**File:** `FinnhubRepository.java`

```java
@Repository
public interface FinnhubRepository extends JpaRepository<FinnhubEntity, Long> {
    // No code needed!
    // Spring automatically creates:
    // - Database connection
    // - CRUD operations (save, findById, delete, etc.)
    // - Query methods
}
```

**What happens:**
- Spring sees `@Repository` annotation
- Spring sees it extends `JpaRepository`
- Spring automatically creates database connection
- Spring provides all CRUD methods for free!

---

## Complete Flow Example

### Request Flow

```
1. HTTP Request arrives
   ↓
2. ProviderController receives request
   ↓
3. Controller needs ProviderService
   ↓
4. Spring provides ProviderServiceImpl (already created)
   ↓
5. Service needs FinnhubRepository
   ↓
6. Spring provides repository (already created with DB connection)
   ↓
7. Repository queries database
   ↓
8. Data flows back: Repository → Service → Controller → HTTP Response
```

### Code Execution Flow

```java
// 1. User makes request: GET /us/realtime
ProviderController.getFinnhub()
    ↓
// 2. Spring injected providerService here
this.providerService.getAllFinnhubData()
    ↓
// 3. Spring injected finnhubRepository here
this.finnhubRepository.findAll()
    ↓
// 4. Database query executes
SELECT * FROM finnhub_entity
    ↓
// 5. Data returns through the chain
return data;
```

---

## Key Concepts

### What is @Autowired?

`@Autowired` tells Spring: **"Please give me this object"**

- Spring automatically finds the right implementation
- Spring creates it if needed
- Spring connects everything together
- You just use it - no `new` keyword needed!

### Bean Lifecycle

| Stage | Description |
|-------|-------------|
| **1. Component Scan** | Spring finds all `@Service`, `@Repository`, `@Controller` classes |
| **2. Bean Creation** | Spring creates instances of these classes |
| **3. Dependency Injection** | Spring connects them using `@Autowired` |
| **4. Ready to Use** | Your application can now use these objects |

### Singleton Pattern

Spring creates **one instance** of each bean and reuses it:
- `ProviderServiceImpl` - created once, used everywhere
- `FinnhubRepository` - created once, used everywhere
- More efficient than creating new objects every time

---

## Benefits of Dependency Injection

### ✅ Without @Autowired (Manual Creation)

```java
// ❌ BAD - You have to do everything yourself
public class ProviderController {
    private ProviderService providerService;
    
    public ProviderController() {
        // Create service
        this.providerService = new ProviderServiceImpl();
        
        // But wait! ProviderServiceImpl needs repositories...
        // You'd have to create those too!
        // Very messy and error-prone!
    }
}
```

### ✅ With @Autowired (Spring Does It)

```java
// ✅ GOOD - Spring does everything automatically
@RestController
public class ProviderController {
    
    @Autowired
    ProviderService providerService;  // Spring creates and provides it!
    
    // That's it! Ready to use!
}
```

### Advantages

1. **Less Code** - No manual object creation
2. **Automatic Wiring** - Spring connects everything
3. **Easy Testing** - Can replace with mocks
4. **Loose Coupling** - Dependencies are injected, not hardcoded
5. **Single Responsibility** - Each class focuses on its job

---

## Summary

### The Magic of @Autowired

When you write:
```java
@Autowired
ProviderService providerService;
```

Spring automatically:
1. ✅ Finds `ProviderServiceImpl` (the implementation)
2. ✅ Creates an instance of it
3. ✅ Puts it into the `providerService` variable
4. ✅ Connects all its dependencies too
5. ✅ You can use it immediately!

### Think of it Like...

**Without Spring:**
- You = Chef who must buy ingredients, cook, and serve

**With Spring:**
- You = Customer who orders food
- Spring = Restaurant that delivers ready-to-eat food

You just order what you need (`@Autowired`), and Spring delivers it ready to use!

---

## Related Files in Your Project

- **Controller:** `stock-data/src/main/java/.../ProviderController.java`
- **Service:** `stock-data/src/main/java/.../ProviderServiceImpl.java`
- **Repository:** `stock-data/src/main/java/.../FinnhubRepository.java`
- **Config:** `stock-data/src/main/java/.../AppConfig.java`

---

## Additional Resources

- [Spring Framework Documentation](https://spring.io/projects/spring-framework)
- [Spring Boot Dependency Injection](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.dependency-injection)


