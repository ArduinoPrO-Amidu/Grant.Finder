const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read users collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Unauthenticated user: cannot read opportunities collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("opportunities").get());
});

test("Authenticated user: can create and read own user profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const profileData = {
    userId: ALICE_UID,
    fullName: "Alice Mensah",
    region: "Greater Accra",
    userType: "Entrepreneur",
    createdAt: new Date(),
  };

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).set(profileData));
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot read or write another user profile", async () => {
  // Seed Bob's profile as admin
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      fullName: "Bob Osei",
      region: "Ashanti",
      userType: "Farmer",
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
  await assertFails(aliceDb.collection("users").doc(BOB_UID).update({ fullName: "Hacked" }));
});

test("Authenticated user: can read published opportunities", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("opportunities").doc("opp_1").set({
      title: "Ghana Youth AgriTech Grant",
      organization: "Ministry of Food and Agriculture",
      category: "Agriculture",
      opportunityStatus: "open",
      verificationStatus: "verified",
      officialUrl: "https://mofa.gov.gh/grants",
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("opportunities").doc("opp_1").get());
  await assertSucceeds(aliceDb.collection("opportunities").get());
});

test("Non-admin user: cannot create opportunities", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("opportunities").doc("opp_fake").set({
    title: "Fake Grant",
    organization: "Unauthorized",
    category: "Business grants",
    opportunityStatus: "open",
    verificationStatus: "verified",
    officialUrl: "https://example.com",
    createdAt: new Date(),
  }));
});

test("Authenticated user: can save opportunity and access own saved subcollection", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const savedData = {
    opportunityId: "opp_1",
    userId: ALICE_UID,
    title: "Ghana Youth AgriTech Grant",
    savedAt: new Date(),
  };

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("saved_opportunities").doc("opp_1").set(savedData)
  );
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("saved_opportunities").doc("opp_1").get()
  );
});

test("Authenticated user: cannot read another user's saved opportunities or applications", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).collection("saved_opportunities").doc("opp_1").set({
      opportunityId: "opp_1",
      userId: BOB_UID,
      title: "Bob's Secret Grant",
      savedAt: new Date(),
    });
    await context.firestore().collection("users").doc(BOB_UID).collection("applications").doc("app_1").set({
      applicationId: "app_1",
      opportunityId: "opp_1",
      userId: BOB_UID,
      opportunityTitle: "Bob's Application",
      status: "Submitted",
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("saved_opportunities").doc("opp_1").get()
  );
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("applications").doc("app_1").get()
  );
});
