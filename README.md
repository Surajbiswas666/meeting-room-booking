
# meeting-room-booking 

A brief description of what this project does and who it's for

┌─────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                             │
│  ┌────────────────────────────────────────────────────────┐    │
│  │         React (Vite) + Tailwind CSS                     │    │
│  │  - Authentication UI      - Booking Calendar            │    │
│  │  - Admin Dashboard        - Reports UI                  │    │
│  │  - Employee Dashboard                                   │    │
│  └────────────────────────────────────────────────────────┘    │
│                            ↕ HTTPS                              │
└─────────────────────────────────────────────────────────────────┘
                                ↕
┌─────────────────────────────────────────────────────────────────┐
│                      API GATEWAY / LOAD BALANCER                │
│                     (Render Platform - Auto)                     │
└─────────────────────────────────────────────────────────────────┘
                                ↕
┌─────────────────────────────────────────────────────────────────┐
│                      APPLICATION LAYER                           │
│  ┌────────────────────────────────────────────────────────┐    │
│  │           Spring Boot REST API                          │    │
│  │                                                          │    │
│  │  ┌──────────────┐  ┌──────────────┐  ┌─────────────┐ │    │
│  │  │ Controllers  │  │   Services   │  │ Repositories│ │    │
│  │  │              │  │              │  │             │ │    │
│  │  │ - Auth       │→ │ - Business   │→ │ - Data      │ │    │
│  │  │ - Booking    │  │   Logic      │  │   Access    │ │    │
│  │  │ - Room       │  │ - Validation │  │ - JPA       │ │    │
│  │  │ - Admin      │  │              │  │             │ │    │
│  │  └──────────────┘  └──────────────┘  └─────────────┘ │    │
│  │                                                          │    │
│  │  │    │
│  └────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
         ↕                    ↕                    ↕
┌────────────────┐  ┌────────────────┐  ┌────────────────────┐
│  DATABASE      │  │  BACKGROUND    │  │  EXTERNAL          │
│                │  │  JOBS          │  │  SERVICES          │
│  PostgreSQL    │  │                │  │                    │
│                │  │  @Scheduled    │  │  ┌──────────────┐ │
│  - Users       │  │  - Recurring   │  │  │ Email (SMTP) │ │
│  - Rooms       │  │    Bookings    │  │  │ Spring Mail  │ │
│  - Bookings    │  │  - Reminders   │  │  └──────────────┘ │
│  - Files       │  │  @Async        │  │  ┌──────────────┐ │
│  - Audit Logs  │  │  - Email Send  │  │  │  AWS S3      │ │
│                │  │  - Reports     │  │  │______________| │
└────────────────┘  └────────────────┘  │
                                        └────────────────────┘
										


  
## API Reference

#### Get all items

```http
  GET /api/items
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `api_key` | `string` | **Required**. Your API key |

#### Get item

```http
  GET /api/items/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | **Required**. Id of item to fetch |

#### add(num1, num2)

Takes two numbers and returns the sum.

Base URL: https://localhost:8080/api

Frontend URL : https://meeting-room-gamma.vercel.app

Authentication:
  POST   /auth/login
  POST   /auth/register
  GET    /auth/profile
  PUT    /auth/profile

Rooms:
  GET    /rooms
  GET    /rooms/{id}
  POST   /rooms                    [ADMIN]
  PUT    /rooms/{id}               [ADMIN]
  DELETE /rooms/{id}               [ADMIN]
  GET    /rooms/search?capacity=10&floor=2

Bookings:
  POST   /bookings                 (creates PENDING)
  GET    /bookings/my-bookings
  GET    /bookings/{id}
  PUT    /bookings/{id}
  DELETE /bookings/{id}
  GET    /bookings/room/{roomId}?date=2025-11-18
  
Admin Booking Management:
  GET    /admin/bookings/pending   [ADMIN]
  GET    /admin/bookings/all       [ADMIN]
  PUT    /admin/bookings/{id}/approve  [ADMIN]
  PUT    /admin/bookings/{id}/reject   [ADMIN]

Recurring Bookings:
  POST   /recurring-bookings
  GET    /recurring-bookings/my-rules
  PUT    /recurring-bookings/{id}
  DELETE /recurring-bookings/{id}

Files:
  POST   /files/upload
  GET    /files/booking/{bookingId}
  DELETE /files/{fileId}

Reports & Analytics:
  GET    /reports/bookings?startDate=...&endDate=...  [ADMIN]
  GET    /analytics/dashboard      [ADMIN]
  GET    /analytics/room-utilization [ADMIN]

Audit:
  GET    /audit/booking/{bookingId} [ADMIN]
  GET    /audit/user/{userId}       [ADMIN]
## Screenshots
## 🟦 Login Page
![Login](https://github.com/Surajbiswas666/meeting-room-booking/blob/e26c36a7f3af460692a890ade7eb8b3434f1ace3/Screenshots/Login.png?raw=true)

## 🟩 Register Page
![Register](https://github.com/Surajbiswas666/meeting-room-booking/blob/e26c36a7f3af460692a890ade7eb8b3434f1ace3/Screenshots/Register.png?raw=true)
## 🟧 Admin Dashboard
![Admin Dashboard](https://github.com/Surajbiswas666/meeting-room-booking/blob/e26c36a7f3af460692a890ade7eb8b3434f1ace3/Screenshots/AdminDashBoard.png?raw=true)
## 🟨 Employee Dashboard
![Employee Dashboard](https://github.com/Surajbiswas666/meeting-room-booking/blob/e26c36a7f3af460692a890ade7eb8b3434f1ace3/Screenshots/EmployeeDashBoard.png?raw=true)





## 🚀 Future Enhancements
1. Improved Email Delivery (Production Ready)

Email notifications (booking confirmations, approvals, reminders) currently work in the local environment using Gmail SMTP.
However, Render does not support Gmail SMTP, which prevents email delivery after deployment.

In future updates, the email module will be enhanced to use a production-ready and Render-compatible email service.

This enhancement will ensure:

Reliable email sending in production

No dependency on Gmail SMTP

Better deliverability, logging, and tracking

➡️ Status: Email works locally ✔️
➡️ Production: Temporarily disabled ✖️
➡️ Plan: Migrate to a cloud email provider and enable full mail functionality


2. Advanced Audit Log Viewer (Frontend)

Audit logs are recorded successfully but will be enhanced with:

Filters (by user / module / date)

Pagination

Export (CSV / PDF)

3. Improved Document Upload System

Future update will support:

Preview uploaded files

Integration with S3 / cloud storage

Larger file constraints

4. Calendar UI Enhancements

Planned UI improvements:

Drag-and-drop booking edits

Improved visualization of conflicts

Color-coded room availability

5. Automated Email Reminders

Once SMTP is configured on Render, scheduled jobs will send:

Upcoming meeting reminders

Recurring booking notifications

Admin alerts for pending approvals

6. Mobile Optimizations

The UI works on mobile but will be enhanced with:

Compact calendar layout

Mobile-first navigation

Optimized forms


7. Admin Analytics Upgrade

Planned features:

Live charts (Recharts)

Heatmaps for room usage

Exportable analytics

9. Improved Security Layer

Upcoming improvements:

Refresh tokens for longer sessions

Optional 2FA

Rate limiting for sensitive endpoints
