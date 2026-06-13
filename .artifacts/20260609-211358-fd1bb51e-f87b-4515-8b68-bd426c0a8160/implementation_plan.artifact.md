# Fix KSP Error and Implement Firebase-Room Synchronization

This plan addresses the KSP build error and implements a robust synchronization mechanism between Room (local) and Firebase (remote) for all data entities (Customers, Vehicles, Items, etc.).

## User Review Required

> [!IMPORTANT]
> - Room version is being updated to `2.7.0-alpha11` to fix a known compatibility issue with Kotlin 2.0.
> - The application will shift to a "Local First" architecture: UI observes Room, and a Repository synchronizes Room with Firebase.

## Proposed Changes

### Build Configuration

#### [build.gradle.kts](file:///D:/Codes/Kotlin/UTS_KelompokTAM/app/build.gradle.kts)
- Update Room dependencies to `2.7.0-alpha11`.

---

### Data Layer (Room Entities & DAOs)

#### [Customers.kt](file:///D:/Codes/Kotlin/UTS_KelompokTAM/app/src/main/java/com/le/uts_tam/data/model/dataclass/Customers.kt)
- Add `firebaseKey` field to the Room entity to track synchronization.
- Ensure all fields used in the UI are present.

#### [CustomerDao.kt](file:///D:/Codes/Kotlin/UTS_KelompokTAM/app/src/main/java/com/le/uts_tam/data/local/dao/CustomerDao.kt)
- Add `upsert` methods and methods to get unsynchronized data.

---

### Repository Layer

#### [FirebaseRepository.kt](file:///D:/Codes/Kotlin/UTS_KelompokTAM/app/src/main/java/com/le/uts_tam/data/repository/FirebaseRepository.kt)
- Integrate `AppDatabase` and its DAOs.
- Implement "Real-time Sync": When Firebase data changes, update Room.
- Implement "Write-through": When saving data, save to Room first, then push to Firebase.

---

### UI Layer (ViewModels)

#### [PelangganViewModel.kt](file:///D:/Codes/Kotlin/UTS_KelompokTAM/app/src/main/java/com/le/uts_tam/ui/screen/pelanggan/viewmodel/PelangganViewModel.kt)
- Change from observing `repository.getCustomers()` (Firebase Flow) to a new method that provides Room data.

#### [ProfilViewModel.kt](file:///D:/Codes/Kotlin/UTS_KelompokTAM/app/src/main/java/com/le/uts_tam/ui/screen/profil/viewmodel/ProfilViewModel.kt)
- Ensure profile updates are synchronized.

## Verification Plan

### Manual Verification
1. **Build Success**: Run `./gradlew :app:kspDebugKotlin` to verify the KSP error is gone.
2. **Data Sync**:
    - Add a customer in the app -> Verify it appears in Firebase.
    - Edit a customer in Firebase -> Verify it updates in the app automatically.
    - Change Profile info -> Verify it persists in both Firebase and local state.
3. **Offline Support (Optional but recommended)**:
    - Turn off internet -> Add a customer -> Verify it shows in the app (local Room).
    - Turn on internet -> Verify it eventually syncs to Firebase.
