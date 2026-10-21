# Verified backend API inventory

Inspected in `backend/meet-minutes-backend/src/main/java/com/meetminutes` on 21 Oct 2026. The API base path is `/api`. `SecurityConfig` permits `/api/auth/**`; all other routes require a valid JWT sent as `Authorization: Bearer <token>`. It has no CORS configuration. The frontend's Vite development proxy forwards `/api` to `http://localhost:8080`.

| Method | Endpoint | Request | Response | Authentication / notes |
|---|---|---|---|---|
| POST | `/auth/register` | JSON `{fullName,email,password,profileImageUrl?}` | `UserResponse`: `id,fullName,email,profileImageUrl,role,isActive` | Public |
| POST | `/auth/login` | JSON `{email,password}` | `{message,token}` | Public |
| GET | `/meetings/my` | None | `MeetingResponse[]` | JWT; meetings returned by current-user service |
| GET | `/meetings` | None | `MeetingResponse[]` | JWT |
| GET | `/meetings/{id}` | None | `MeetingResponse` | JWT |
| POST | `/meetings` | JSON `{title,description?,meetingDate,startTime?,endTime?,location?,meetingLink?}` | `MeetingResponse` (201) | JWT |
| PUT | `/meetings/{id}` | Same JSON as create | `MeetingResponse` | JWT |
| DELETE | `/meetings/{id}` | None | Empty (204) | JWT |
| GET | `/meetings/invitations` | None | `InvitationResponse[]`: `meetingId,title,description,meetingDate,startTime,endTime,location,meetingLink,invitedById,invitedByName,status` | JWT; current user's pending invitations |
| GET | `/meetings/{meetingId}/participants` | None | `ParticipantResponse[]`: `id,userId,userName,userEmail,status` | JWT; owner or accepted participant |
| POST | `/meetings/{meetingId}/participants` | JSON `{userId}` | `ParticipantResponse` | JWT; meeting owner |
| DELETE | `/meetings/{meetingId}/participants/{userId}` | None | Empty (204) | JWT; meeting owner |
| POST | `/meetings/{meetingId}/participants/accept` | No body | `ParticipantResponse` | JWT; invited user |
| POST | `/meetings/{meetingId}/participants/decline` | No body | `ParticipantResponse` | JWT; invited user |
| POST | `/meetings/{meetingId}/recordings` | Multipart form part `file` | `MeetingRecordingResponse`: `id,meetingId,fileName,fileType,fileSize,processingStatus,uploadedAt` (201) | JWT |
| GET | `/meetings/{meetingId}/recordings` | None | `MeetingRecordingResponse[]` | JWT |
| POST | `/meetings/{meetingId}/recordings/{recordingId}/transcript` | No body | `TranscriptResponse`: `id,recordingId,transcriptText,status,createdAt,updatedAt` (201) | JWT; triggers transcription |
| POST | `/meetings/{meetingId}/generate-minutes` | JSON `{transcriptText}` | `{message,meetingId,minutesId,summary,discussionPoints,decisions}` | JWT; also generates action items in backend service |
| GET | `/meetings/{meetingId}/action-items` | None | `ActionItemResponse[]`: `id,meetingId,task,responsiblePerson,deadline,status,assignedToUserId,assignedToName,createdAt,updatedAt` | JWT; owner or accepted participant |
| POST | `/meetings/{meetingId}/action-items` | JSON `{task,responsiblePerson?,deadline?}` | `ActionItemResponse` (201) | JWT; meeting owner |
| PUT | `/meetings/{meetingId}/action-items/{actionItemId}/assign` | JSON `{userId}` | Success map with action item and assignee fields | JWT; meeting owner; assignee must be a participant |
| DELETE | `/meetings/{meetingId}/action-items/{actionItemId}/assignment` | None | Success map | JWT; meeting owner |
| DELETE | `/meetings/{meetingId}/action-items/{actionItemId}` | None | Empty (204) | JWT; meeting owner |
| PATCH | `/meetings/{meetingId}/action-items/{actionItemId}/status` | JSON `{status}` (`PENDING`, `IN_PROGRESS`, `COMPLETED`) | Success map with updated status | JWT; meeting owner or task assignee |
| GET | `/meetings/{meetingId}/action-items/my-tasks` | None | `MyTaskResponse[]` | JWT; duplicate scoped route in `ActionItemAssignmentController`; frontend uses the dedicated route below |
| GET | `/action-items/my-tasks` | None | `MyTaskResponse[]`: `id,meetingId,meetingTitle,task,responsiblePerson,deadline,status,createdAt,updatedAt` | JWT; assigned tasks for current user |
| GET | `/users` | None | `UserResponse[]` | JWT; `ADMIN` role only; not used by regular-user UI |
| POST | `/users` | JSON user request | `UserResponse` | JWT; `/api/users` requires `ADMIN` |

Meeting responses contain `id,title,description,meetingDate,startTime,endTime,location,meetingLink,status,createdBy,createdByName,createdAt,updatedAt`. Login returns only `message` and `token`; the token claims are email in `sub`, `userId`, and `role`. No current-user profile endpoint was found. The source also contains `/api/hello`, `/api/test`, and `/api/test/gemini` test routes; they are not part of the frontend workflow. No endpoint to fetch an existing transcript or saved minutes was found. The frontend displays the transcript returned by transcription and the minutes returned immediately after generation. Participant invitation and task assignment use registered user IDs because regular users cannot access the admin-only user list.
