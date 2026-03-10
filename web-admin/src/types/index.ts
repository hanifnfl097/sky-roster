/**
 * SkyRoster shared TypeScript types for crew domain.
 * Maps to the backend domain models and API response shapes.
 */

// --- Enums ---

export type CrewRole = "CAPTAIN" | "FIRST_OFFICER" | "PURSER" | "FLIGHT_ATTENDANT";

export type CrewCategory = "FLIGHT_CREW" | "CABIN_CREW";

export type CrewStatus = "ACTIVE" | "GROUNDED" | "ON_LEAVE";

export type DocumentType = "MEDEX" | "ATPL" | "CPL" | "INSTRUMENT_RATING" | "SEP" | "SIM_CHECK" | "TYPE_RATING_CERT";

export type DocumentStatus = "VALID" | "EXPIRING_SOON" | "EXPIRED";

export type PairingStatus = "DRAFT" | "ASSIGNED" | "PUBLISHED";

export type FlightStatus = "SCHEDULED" | "BOARDING" | "DEPARTED" | "ARRIVED" | "CANCELLED";

export type SignOnStatus = "SIGNED_ON" | "PENDING" | "NO_SHOW" | "SICK";

export type DisruptionType = "NO_SHOW" | "SICK" | "DELAYED" | "CANCELLED";

export type VoyageReportStatus = "SUBMITTED" | "APPROVED" | "REJECTED";

export type NotificationSeverity = "INFO" | "WARNING" | "CRITICAL";

// --- Interfaces ---

export interface CrewMember {
    id: string;
    userId: string;
    staffId: string;
    firstName: string;
    lastName: string;
    crewRole: CrewRole;
    crewCategory: CrewCategory;
    baseStation: string;
    status: CrewStatus;
    groundingReason?: string;
    groundedAt?: string;
    createdAt: string;
    updatedAt: string;
}

export interface CrewDocument {
    id: string;
    crewMemberId: string;
    documentType: DocumentType;
    documentNumber?: string;
    issueDate: string;
    expiryDate: string;
    status: DocumentStatus;
    fileUrl?: string;
    createdAt: string;
    updatedAt: string;
}

export interface FtlSummary {
    crewId: string;
    sevenDayHours: number;
    sevenDayLimit: number;
    monthlyHours: number;
    monthlyLimit: number;
    yearlyHours: number;
    yearlyLimit: number;
    hoursSinceLastBlockOn: number;
}

export interface Flight {
    id: string;
    pairingId: string;
    flightNumber: string;
    originAirportId: string;
    destinationAirportId: string;
    aircraftTypeId: string;
    scheduledDeparture: string;
    scheduledArrival: string;
    actualBlockOff?: string;
    actualBlockOn?: string;
    legSequence: number;
    status: FlightStatus;
}

export interface Pairing {
    id: string;
    pairingCode: string;
    startDate: string;
    endDate: string;
    aircraftTypeId: string;
    status: PairingStatus;
    flights: Flight[];
    assignments: RosterAssignment[];
}

export interface RosterAssignment {
    id: string;
    crewMemberId: string;
    pairingId: string;
    assignedRole: CrewRole;
    status: string;
    assignedAt: string;
    crewMember?: CrewMember;
}

export interface SystemNotification {
    id: string;
    notificationType: string;
    severity: NotificationSeverity;
    title: string;
    message: string;
    isRead: boolean;
    createdAt: string;
}

// --- API Response Wrapper ---

export interface PaginatedResponse<T> {
    data: T[];
    total: number;
    page: number;
    size: number;
    totalPages: number;
}
